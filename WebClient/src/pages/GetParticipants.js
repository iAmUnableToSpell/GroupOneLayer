import "../styles/Page.css"
import axios from "axios"
import { useEffect, useState } from "react"
import toast from 'react-hot-toast'

function GetParticipants() {

  const [participants, setParticipants] = useState([]);
  const [eventID, setEventId] = useState('')

  useEffect(() => {
    updateParticipants();
  }, [])

  const updateParticipants = async () => {
    let jsonObject
    if (eventID == "") {
      console.log(eventID)
      jsonObject = {}
    } else {
      jsonObject = {
        "eventID" : eventID.toLowerCase()
      }
    }

    axios({
      method: 'POST',
      url: 'http://ec2-54-145-190-43.compute-1.amazonaws.com:3000/api/list-participants',
      timeout: 10000,
      withCredentials : false,
      headers : {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      data : JSON.stringify(jsonObject)  
    })
    .then((response) => {
      console.log(response.data)
      setParticipants(response.data.participants)
    }).catch(error => {
      toast.error("Error connecting to server: "+ error);
    })
  }

  const getparticipantsList = () => {
    return(participants.map(participant => {
    return(
      <div className="list-entry">
        <div className="list-data">{participant.name}</div>
        <div className="list-data">{participant.email}</div>
        <div className="list-data">{participant.eventID}</div>
        <div className="list-data">{participant.uuid}</div>
      </div>
    )}))
  }

  const getLegend = () => {
    if (participants.length > 0) {
    return (
    <div className="legend">
         <div className="list-data">Name</div>
              <div className="list-data">Email</div>
              <div className="list-data-wide">event UUID</div>
              <div className="list-data-wide">participant UUID</div>
    </div>
    )} else return <p>No Data To Display</p> 
  }

  return (
    <div className="wrapper">
        <h1 className="title">List Participants</h1>
        <div className="input-bar">
          <div className="list-container">
            <div className="search-container">
              Event ID:
              <div className="seachbar-wrapper">
                <input className='search-input' maxlength='36' type='text' value={eventID}  onChange={(e) => {setEventId(e.target.value)}} 
                  pattern = '^[0-9a-fA-F]{8}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{12}$'/>
                <span class="search-placeholder">XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX</span>
              </div>
              <button onClick={updateParticipants}>Search</button>
            </div>
            {getLegend()}
            {getparticipantsList()}
          </div>
        </div>
      </div>
  );
}

export default GetParticipants;
