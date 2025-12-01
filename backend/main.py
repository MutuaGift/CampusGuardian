from fastapi import FastAPI
from pydantic import BaseModel
from pymongo import MongoClient
from bson import ObjectId  # <--- Critical Import for finding IDs
import datetime

app = FastAPI()

# --- DATABASE CONNECTION ---
# Connect to MongoDB running on this machine
client = MongoClient("mongodb://localhost:27017/")
db = client["campus_guardian_db"]
rides_collection = db["active_rides"]

# --- DATA MODELS ---
class RideRequest(BaseModel):
    student_name: str
    pickup_location: str
    destination: str

# --- ENDPOINTS ---

# 1. Health Check
@app.get("/")
def read_root():
    return {"status": "Active", "message": "CampusGuardian Brain is Online"}

# 2. Request a Ride (Create)
@app.post("/request-ride")
def create_ride(request: RideRequest):
    # Create the document
    ride_data = {
        "student": request.student_name,
        "pickup": request.pickup_location,
        "destination": request.destination,
        "status": "PENDING",
        "timestamp": datetime.datetime.now()
    }
    
    # Save to MongoDB
    result = rides_collection.insert_one(ride_data)
    
    print(f"New Ride Saved! ID: {result.inserted_id}")
    
    return {
        "status": "Success",
        "ride_id": str(result.inserted_id),
        "message": f"Ride saved! Waiting for drivers..."
    }

# 3. Get All Active Rides (Read)
@app.get("/active-rides")
def get_rides():
    # Fetch all documents
    rides_cursor = rides_collection.find({})
    
    clean_rides = []
    for ride in rides_cursor:
        # Convert the weird Mongo ID to a normal String for JSON
        ride["_id"] = str(ride["_id"]) 
        clean_rides.append(ride)
    
    return clean_rides

# 4. Accept a Ride (Update)
@app.put("/accept-ride/{ride_id}")
def accept_ride(ride_id: str):
    print(f"Attempting to accept ride: {ride_id}")
    
    try:
        # Find the ride by ID and change status to IN_PROGRESS
        result = rides_collection.update_one(
            {"_id": ObjectId(ride_id)}, # Convert string ID to Mongo ID
            {"$set": {"status": "IN_PROGRESS"}}
        )
        
        if result.modified_count == 1:
            return {"status": "Success", "message": "Ride Accepted! Go pick them up."}
        else:
            return {"status": "Error", "message": "Ride not found or already accepted"}
            
    except Exception as e:
        return {"status": "Error", "message": str(e)}
