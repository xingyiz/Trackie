import numpy as np

from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_squared_error

goodssids = [x.strip() for x in open('B2L2_good_ssids.txt').read().split(',')[:-1]]
NUM_GOOD_SSID = len(goodssids)

import json
LEGAL_POINTS = json.load(open('B2L2_LEGAL_POINTS.json'))['LEGAL_POINTS']

def closestLegalId(unknown):
  unknown = np.array(unknown)
  dist_2 = np.sum((LEGAL_POINTS - unknown)**2, axis=1)
  return np.argmin(dist_2)

import pandas as pd
df0 = pd.read_json("trackie_data.json")
df0.describe()

# CREATE NEW DATAFRAME WITH GOOD SSIDS ONLY 
target_cols = ['locX', 'locY']
rssicols = [f'r{i}v' for i in range(NUM_GOOD_SSID)]
rssicols.extend([f'r{i}rssi' for i in range(NUM_GOOD_SSID)])

all_cols = []
all_cols.extend(target_cols)
all_cols.extend(rssicols)

df1 = pd.DataFrame(columns=all_cols)

for id in df0:
  datarow = {
      'locX': df0[id]['location']['x'],
      'locY': df0[id]['location']['y']
  }
  cleanrssi = {}
  for ssid in df0[id].data:
    cleanrssi[ssid] = np.mean(df0[id].data[ssid])
  for i in range(len(goodssids)):
    if goodssids[i] in cleanrssi:
      datarow[f'r{i}v']     = 1
      # NORMALISE WITH -100 
      datarow[f'r{i}rssi']  = cleanrssi[goodssids[i]] / -100
    else:
      datarow[f'r{i}v']     = 0
      datarow[f'r{i}rssi']  = -1
  df1 = df1.append(datarow, ignore_index=True)

# SET X AND Y AND SPLIT DATA INTO TRAINING AND TEST SET 

X = df1[rssicols]
y = df1[['locX', 'locY']]
y_x = df1['locX']
y_y = df1['locY']

y_legal = []
for row in y.to_numpy():
  y_legal.append(closestLegalId(np.array([row[0], row[1]])))
y_legal = np.array(y_legal)
y_legal.shape

from sklearn.ensemble import ExtraTreesClassifier

xtclassifier = ExtraTreesClassifier(n_estimators=256).fit(X, y_legal)

print(xtclassifier.score(X, y_legal))
print(mean_squared_error(y_legal, xtclassifier.predict(X)))

import matplotlib.pyplot as plt
img = plt.imread("bg.jpg")

from fastapi import Request, FastAPI

app = FastAPI()

import time

@app.post("/pred")
async def get_body(request: Request):
    lmao = await request.json()
    pred = xtclassifier.predict(lmao['instances'])
    return_json = '{"predictions": ' + str(pred) + '}'
    print(return_json)
 
    plt.clf()

    plt.imshow(img, extent=[0, 1, 0, 1])

    xx, yy = LEGAL_POINTS[pred[0]]
    plt.plot(xx, 1 - yy,'bo-')
    plt.savefig(f'imgs/pred{time.time()}.jpg')

    return json.loads(return_json)