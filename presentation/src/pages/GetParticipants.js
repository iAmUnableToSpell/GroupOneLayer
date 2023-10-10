import "../styles/Page.css"
import axios from "axios"
import { useEffect, useState } from "react";

function GetParticipants() {

  const [participants, setParticipants] = useState([]);

  useEffect(() => {
    updateParticipants();
  }, [])

  const updateParticipants = async () => {
    // axios.get("localhost:5000/get-participants").then((response) => {
    //   setParticipants(response)
    // })
    // temp dummy participants for testing
    setParticipants([{
      name: 'name1',
      email: 'email',
      e_uuid: '550e8400-e29b-41d4-a716-446655440001',
      uuid: '550e8400-e29b-41d4-a716-446655440000'
    },{
      name: 'name2',
      email: 'email',
      e_uuid: '550e8400-e29b-41d4-a716-446655440002',
      uuid: '550e8400-e29b-41d4-a716-446655440000'
    },{
      name: 'name3',
      email: 'email',
      e_uuid: '550e8400-e29b-41d4-a716-446655440003',
      uuid: '550e8400-e29b-41d4-a716-446655440000'
    }])
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
