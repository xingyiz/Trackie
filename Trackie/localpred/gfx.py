import matplotlib.pyplot as plt
import numpy as np

import json
LEGAL_POINTS = json.load(open('B2L2_LEGAL_POINTS.json'))['LEGAL_POINTS']

def closestLegalId(unknown):
  unknown = np.array(unknown)
  dist_2 = np.sum((LEGAL_POINTS - unknown)**2, axis=1)
  return np.argmin(dist_2)

img = plt.imread("bg.jpg")

plt.imshow(img, extent=[0, 1, 0, 1])

xx, yy = LEGAL_POINTS[closestLegalId([0.45, 0.45])]
plt.plot(xx, yy,'bo-')

plt.show()