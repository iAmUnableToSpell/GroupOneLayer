import "../styles/Page.css"
import { useState } from "react";
import axios from 'axios'

function Event() {

  const [title, setTitle] = useState(null)
  const [desc, setDesc] = useState(null)
  const [email, setEmail] = useState(null)
  const [date, setDate] = useState(null)
  const [time, setTime] = useState(null)
  const [uuid, setUUID] = useState(null)
  

  const onSubmit = async (event) => {
    event.preventDefault();
    const jsonObject = {
      "date": date,
      "time": time,
      "email": email,
      "title": title,
      "desc": desc,
      "uuid": uuid == "" ? null : uuid
    }
    axios.post("localhost:5000/event", jsonObject).then((response) => {
      console.log(response)
      //TODO handle error codes
    })
  }

  return (
    <div className="wrapper">
        <h1 className="title">New Event</h1>
        <div className="input-bar">
          <form onSubmit={onSubmit} className="form">
            <label>
              event date
              <input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
            </label>
            <label>
              event time
              <input type="time" value={time} onChange={(e) => setTime(e.target.value)} />
            </label>
            <label>
              host email
              <input type="text" value={email} onChange={(e) => setEmail(e.target.value)} />
            </label>
            <label>
              event title
              <input type="text" value={title} onChange={(e) => setTitle(e.target.value)} />
            </label>
            <label>
              event description
              <input type="text" value={desc} onChange={(e) => setDesc(e.target.value)} />
            </label>
            <label>
              UUID (optional)
              <input type="text" value={uuid} onChange={(e) => setUUID(e.target.value)} />
            </label>
            <input type="submit" value={"Create"} />
          </form>
        </div>
      </div>
  );
}

export default Event;
