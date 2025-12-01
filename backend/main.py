from fastapi import FastAPI
from pydantic import BaseModel
from pymongo import MongoClient # The Database Connector
import datetime

app = FastAPI()

# 1. Connect to MongoDB
# It runs on localhost port 27017 by default
client = MongoClient("mongodb://localhost:27017/")
db = client["campus_guardian_db"]  # Create a Database named 'campus_guardian_db'
rides_collection = db["active_rides"] # Create a Collection (Folder) named 'active_rides'

# 2. Define Data Shape
class RideRequest(BaseModel):
    student_name: str
    pickup_location: str
    destination: str

@app.get("/")
def read_root():
    return {"status": "Active", "message": "CampusGuardian Brain + Memory Online"}

@app.post("/request-ride")
def create_ride(request: RideRequest):
    # A. Create the document to save
    ride_data = {
        "student": request.student_name,
        "pickup": request.pickup_location,
        "destination": request.destination,
        "status": "PENDING",
        "timestamp": datetime.datetime.now()
    }
    
    # B. SAVE IT TO MONGO (The Magic Line)
    result = rides_collection.insert_one(ride_data)
    
    print(f"Saved to DB! ID: {result.inserted_id}")
    
    # C. Send success back to phone
    return {
        "status": "Success",
        "ride_id": str(result.inserted_id), # Send back the real Mongo ID
        "message": f"Ride saved! Waiting for drivers..."
    }
# 4. The Driver View (GET ALL)
@app.get("/active-rides")
def get_rides():
    # A. Fetch all documents from Mongo
    rides_cursor = rides_collection.find({})
    
    # B. Convert them to a clean List
    clean_rides = []
    for ride in rides_cursor:
        # CRITICAL: Convert the weird Mongo ID to a normal String
        ride["_id"] = str(ride["_id"]) 
        clean_rides.append(ride)
    
    # C. Send the list
    return clean_rides
