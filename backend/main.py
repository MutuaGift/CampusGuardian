from fastapi import FastAPI
from pydantic import BaseModel
from pymongo import MongoClient
from bson import ObjectId
import datetime

app = FastAPI()

# --- DATABASE CONNECTION ---
client = MongoClient("mongodb://localhost:27017/")
db = client["campus_guardian_db"]
rides_collection = db["active_rides"]

# --- DATA MODELS ---
class RideRequest(BaseModel):
    student_name: str
    pickup_location: str
    pickup_lat: float
    pickup_lng: float
    destination: str
    travel_mode: str

class SOSSignal(BaseModel):
    lat: float
    lng: float

# --- ENDPOINTS ---

@app.get("/")
def read_root():
    return {"status": "Active", "message": "CampusGuardian Brain is Online"}

@app.post("/request-ride")
def create_ride(request: RideRequest):
    ride_data = {
        "student": request.student_name,
        "pickup": request.pickup_location,
        "pickup_lat": request.pickup_lat,
        "pickup_lng": request.pickup_lng,
        "destination": request.destination,
        "travel_mode": request.travel_mode,
        "status": "PENDING",
        "timestamp": datetime.datetime.now()
    }
    result = rides_collection.insert_one(ride_data)
    print(f"New {request.travel_mode} Request from {request.student_name}")
    return {"status": "Success", "ride_id": str(result.inserted_id), "message": "Request Sent!"}

# THE UPDATED SOS ENDPOINT
@app.post("/sos")
def trigger_sos(signal: SOSSignal):
    # We format this exactly like a normal ride so the dashboard can read it
    alert_data = {
        "student": "🚨 SOS ALERT 🚨",  # Special Name
        "pickup": "EMERGENCY LOCATION",
        "pickup_lat": signal.lat,
        "pickup_lng": signal.lng,
        "destination": "N/A",
        "travel_mode": "SOS",
        "status": "SOS_PENDING",       # Special Status
        "timestamp": datetime.datetime.now()
    }
    
    rides_collection.insert_one(alert_data)
    print(f"!!! SOS RECEIVED !!! Lat: {signal.lat}, Lng: {signal.lng}")
    return {"status": "Alert Sent", "message": "Security has been notified!"}

@app.get("/active-rides")
def get_rides():
    # Return everything so we see normal rides AND SOS alerts
    rides_cursor = rides_collection.find({})
    clean_rides = []
    for ride in rides_cursor:
        ride["_id"] = str(ride["_id"]) 
        clean_rides.append(ride)
    return clean_rides

@app.put("/accept-ride/{ride_id}")
def accept_ride(ride_id: str):
    try:
        result = rides_collection.update_one(
            {"_id": ObjectId(ride_id)}, 
            {"$set": {"status": "IN_PROGRESS"}}
        )
        if result.modified_count == 1:
            return {"status": "Success", "message": "Mission Accepted!"}
        else:
            return {"status": "Error", "message": "Ride not found"}
    except Exception as e:
        return {"status": "Error", "message": str(e)}

@app.get("/rides/search")
def search_rides(destination: str):
    query = {"destination": {"$regex": destination, "$options": "i"}}
    rides_cursor = rides_collection.find(query)
    clean_rides = []
    for ride in rides_cursor:
        ride["_id"] = str(ride["_id"]) 
        clean_rides.append(ride)
    return clean_rides
