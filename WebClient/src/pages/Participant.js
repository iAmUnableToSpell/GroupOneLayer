import "../styles/Page.css"
import { useState, useRef } from "react"
import axios from 'axios'
import toast from 'react-hot-toast'

function Participant() {

  const [name, setName] = useState("")
  const [eventID, setEventID] = useState("")
  const [email, setEmail] = useState("")
  const [uuid, setUUID] = useState("")
  const uuidref = useRef(null)    
  
  const onSubmit = async (event) => {
    event.preventDefault();
    const valid = await axios({
      method: 'GET',
      url: 'http://ec2-54-145-190-43.compute-1.amazonaws.com:3000/api/list-events',
      timeout: 10000,
      headers : {
        'Content-Type': 'application/x-www-form-urlencoded',
      },  
      withCredentials : false
    })
    .then((response) => {
      const exist = response.data.events.some((e) => {
        const input = eventID.toLowerCase()
        const match = e.uuid.toLowerCase()
        return match == input
      })
      return exist;
    }).catch(error => {
      toast.error("Error connecting to server: " + error);
    }) 

    if (v alid) {
      const jsonObject = {
        "name" : name,
        "eventID": eventID.toLowerCase(),
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
    } else {
      toast.error("Invalid UUID")
    }
  }

    return (
        <div className="wrapper"> 
        <h1 className="title">New Participant</h1>
        <div className="input-bar">
          <form onSubmit={onSubmit} className="form">
            <label>
              name
              <input type="text" value={name} onChange={(e) => setName(e.target.value)} maxlength='600' required/>
            </label>
            <label>
              event ID
              <input ref={uuidref} type="text" value={eventID} onChange={(e) => setEventID(e.target.value)} placeholder='00000000-0000-0000-0000-000000000000'
                pattern='^[0-9a-fA-F]{8}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{12}$' required/>
            </label>
            <label>
              email
              <input type="text" value={email} onChange={(e) => setEmail(e.target.value)} 
                 pattern = '[a-z0-9._%+\-]+@[a-z0-9.\-]+\.[a-z]{2,}$' required/>
            </label>
            <label>
              UUID (optional)
              <input type="text" value={uuid} onChange={(e) => setUUID(e.target.value)} placeholder='00000000-0000-0000-0000-000000000000'
                pattern='^[0-9a-fA-F]{8}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{12}$'/>
            </label>
            <input type="submit" value={"Create"} />
          </form>
        </div>
      </div>
    );
  }
  
  export default Participant;