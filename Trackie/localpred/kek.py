from fastapi import Request, FastAPI

app = FastAPI()

@app.post("/pred")
async def get_body(request: Request):
    lmao = await request.json()
    print(">>>", lmao)
    return lmao