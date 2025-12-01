from fastapi import FastAPI

# Initialize the App (The "Brain")
app = FastAPI()

# The Logic
# When someone visits the root URL ("/"), do this:
@app.get("/")
def read_root():
    return {"status": "Active", "message": "CampusGuardian Brain is Online"}
