import "../styles/Page.css"
import axios from "axios"
import { useEffect, useState } from "react";

function GetParticipants() {

  const [participants, setParticipants] = useState([]);

  useEffect(() => {
    updateParticipants();
  }, [])

  const updateParticipants = async () => {
    try {
      axios.get("http://ec2-54-158-35-93.compute-1.amazonaws.com/get-participants").then((response) => {
        console.log('req')
        console.log(response)
        setParticipants(response)
      })
    } catch (e) {
      console.log(e)
    }
  }

  const getparticipantsList = () => {
    return(participants.map(participant => {
    return(
      <div className="list-entry">
        <div className="list-data">{participant.name}</div>
        <div className="list-data">{participant.email}</div>
        <div className="list-data">{participant.e_uuid}</div>
        <div className="list-data">{participant.uuid}</div>
      </div>
    )}))
  }

  return (
    <div className="wrapper">
        <h1 className="title">List Participants</h1>
        <div className="input-bar">
          <div className="list-container">
            <div className="legend">
              <div className="list-data">Name</div>
              <div className="list-data">Email</div>
              <div className="list-data">event UUID</div>
              <div className="list-data">participant UUID</div>
            </div>
            {getparticipantsList()}
          </div>
        </div>
      </div>
  );
}

export default GetParticipants;
