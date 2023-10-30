import "../styles/Page.css"
import { useState } from "react"
import axios from 'axios'
import toast from 'react-hot-toast'

function Participant() {

  const [name, setName] = useState("")
  const [eventID, setEventID] = useState("")
  const [email, setEmail] = useState("")
  const [uuid, setUUID] = useState("")
  
  const onSubmit = async (event) => {
    event.preventDefault();
    const jsonObject = {
      "name" : name,
      "eventID": eventID,
      "email": email,
      "uuid": uuid == "" ? null : uuid
    }
    axios({
      method: 'POST',
      url: 'http://ec2-54-145-190-43.compute-1.amazonaws.com:3000/api/participant',
      withCredentials : false,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      data: JSON.stringify(jsonObject)
    })
    .then((response) => {
      if (response.status != 200) {
        toast.error("Invalid Participant");
      } else {
        toast.success("Participant Created")
      }
    }).catch(error => {
      toast.error("Error connecting to server: " + error);
    })
  }
    return (
        <div className="wrapper"> 
        <h1 className="title">New Participant</h1>
        <div className="input-bar">
          <form onSubmit={onSubmit} className="form">
            <label>
              name
              <input type="text" value={name} onChange={(e) => setName(e.target.value)} />
            </label>
            <label>
              event ID
              <input type="text" value={eventID} onChange={(e) => setEventID(e.target.value)} placeholder='00000000-0000-0000-0000-000000000000'/>
            </label>
            <label>
              email
              <input type="text" value={email} onChange={(e) => setEmail(e.target.value)} />
            </label>
            <label>
              UUID (optional)
              <input type="text" value={uuid} onChange={(e) => setUUID(e.target.value)} placeholder='00000000-0000-0000-0000-000000000000'/>
            </label>
            <input type="submit" value={"Create"} />
          </form>
        </div>
      </div>
    );
  }
  
  export default Participant;