from flask import Flask, request
import requests as rq
import uuid 
import datetime
import re
import json

TITLE_MAX_LENGTH = 255
DESCRIPTION_MAX_LENGTH = 600

PERSISTENCE_HOST = 'localhost'
PERSISTENCE_PORT = '6336'

app = Flask(__name__)

def _validateEmail(email: str) -> str:
    emailRegex = r'\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,7}\b'
    if not re.match(emailRegex, email):
        raise ValueError(f"Email {email} not a valid email")
    return email
def _validateDateTime(date: str, time: str) -> datetime.datetime: 
    if not (matches:= re.fullmatch(r'(\d{4})-(\d{2})-(\d{2})', date)):
        raise ValueError(f"Date {date} must be in the format YYYY-MM-DD")
    year, month, day = (int(m) for m in matches.group(1 ,2, 3))

    if not (matches:= re.fullmatch(r"(\d{1,2}):(\d{1,2}) (AM|PM)", time)):
        raise ValueError(f"Time {time} must be in the form \'HH:mm AM|PM\'")
    if matches.group(3).lower() == 'pm' and matches.group(1) != '12':
        hour = int(matches.group(1)) + 12
    elif matches.group(1) == '12' and matches.group(3).lower() == 'am':
        hour = 0
    else:
        hour = int(matches.group(1))
    minute = int(matches.group(2))
    #if not (0 < minute < 59):
    #    raise ValueError(f'Minute must be in range [0, 59], was {minute}')

    return datetime.datetime(year, month, day, hour, minute)
   

class Event:
    def __init__(self,
                 uid: uuid.UUID, 
                 date: datetime.datetime, 
                 title: str,
                 desc: str,
                 hEmail: str):
        self.uid = uid
        self.date = date
        self.title = title
        self.desc = desc
        self.hEmail = hEmail
        pass
    def create( date: str, 
                time:str,
                title: str,
                desc: str,
                hEmail: str,
                uid=None ):
        print(date)
        if not uid:
            uid = uuid.uuid4()
        else:
            uid = uuid.UUID(hex = uid)
        if len(title) > TITLE_MAX_LENGTH:
            raise ValueError(f"Title is too long, must be less than {TITLE_MAX_LENGTH + 1} characters")
        if len(desc) > DESCRIPTION_MAX_LENGTH:
            raise ValueError(f"Description is too long, must be less than {DESCRIPTION_MAX_LENGTH + 1} characters")
        return Event(
            uid = uid,
            date = _validateDateTime(date, time),
            hEmail = _validateEmail(hEmail),
            title = title,
            desc = desc) 

    def __str__(self):
        return f'''
        uuid: {str(self.uid)}
        date: {str(self.date)}
        title: {self.title}
        desc: {self.desc}
        hEmail: {self.hEmail}
        '''
    def __dict__(self):
        return {
            'uid': f'{self.uid}',
            'date': f'{self.date}',
            'title': self.title, 
            'desc': self.desc,
            'hEmail': self.hEmail
        }
class Participant:
    def __init__(self,
                 uid : uuid.UUID,
                 eventId: str,
                 name: str,
                 email: str):
        self.uid = uid
        self.eventId = eventId
        self.name = name
        self.email = email
    def create(eventId: str, name: str, email: str, uid = None) :
        if not uid:
            uid = uuid.uuid4()
        else:
            uid = uuid.UUID(hex = uid)
        return Participant(uid,
                           eventId,
                           name, 
                           _validateEmail(email))
    def __str__(self) -> str:
        return f'''
        uuid: {str(self.uid)}
        eventId: {self.eventId}
        name: {self.name}
        email: {self.email}
        '''
    def __dict__(self) -> dict:
        return {
            'uuid': str(self.uid),
            'eventId': self.eventId,
            'name': self.name,
            'email': self.email
        }


@app.route('/add-event', methods=['POST'])
def addEvent():
    
    data = request.get_json()
    uid = data.get('uid', None)
    if not(date := data.get('date', None)):
        print(date)
        return 'Missing date', 404
    if not(time := data.get('time', None)):
        return 'Missing time', 404
    if not(title := data.get('title', None)):
        return 'Missing title', 404
    if not(desc := data.get('desc', None)):
        return "Missing description", 404
    if not (hEmail := data.get('email', None)):
        return "Missing host email", 404
    event = Event.create(date, time, title, desc, hEmail, uid)
    print(event)
    return str(event), 200

@app.route('/add-participant', methods=['POST'])
def addParticipant():
    data = request.get_json()
    uid = data.get("uid", None)
    if not (eventId := data.get('eventId')):
        return 'Missing event ID', 404
    if not (name := data.get('name')):
        return 'Missing name', 404
    if not (email := data.get('email')):
        return 'Missing email', 404
    participant = Participant.create(
        eventId=data.get('eventId'),
        name = data.get('name'),
        email = data.get('email'),
        uid = request.form.get('uid', None) 
    )
    print(participant)
    pushParticipant(participant)
    return str(participant), 200


@app.route('/list-events', methods=['GET'])
def listEvents():
    return getEvents()

@app.route('/list-participants', methods=['GET'])
def listParticipants():
    if not (eventId := request.args.get('eventId')):
        return "Must provide an event id", 404 
    return getParticipants(eventId)

def pushEvent(event):
    pass
def pushParticipant(participant):
    pass
def getParticipants(eventId):
    return rq.get(f'{PERSISTENCE_HOST}:{PERSISTENCE_PORT}/get-participants?eventUUID={eventId}').content
def getEvents():
    return rq.get(f'{PERSISTENCE_HOST}:{PERSISTENCE_PORT}/get-events').content
    
HOST = "localhost"
PORT = 5000  
if __name__ == "__main__":
    app.run(debug=True, host=HOST, port=PORT)