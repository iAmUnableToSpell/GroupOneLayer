import "../styles/Page.css"
import { useState } from "react"

function Participant() {

  const [name, setName] = useState("")
  const [eventID, setEventID] = useState("")
  const [email, setEmail] = useState("")
  const [uuid, setUUID] = useState("")
  

  const onSubmit = (event) => {
    console.log(event)
  }

    return (
        <div className="wrapper">
        <h1 className="title">Participant</h1>
        <div className="input-bar">
          <form onSubmit={onSubmit} className="form">
            <label>
              name
              <input type="text" value={name} onChange={(e) => setName(e.target.value)} />
            </label>
            <label>
              event ID
              <input type="text" value={eventID} onChange={(e) => setEventID(e.target.value)} />
            </label>
            <label>
              email
              <input type="text" value={email} onChange={(e) => setEmail(e.target.value)} />
            </label>
            <label>
              UUID (optional)
              <input type="text" value={uuid} onChange={(e) => setUUID(e.target.value)} />
            </label>
            <input type="submit" value={"Create\n Event"} />
          </form>
        </div>
      </div>
    );
  }
  
  export default Participant;