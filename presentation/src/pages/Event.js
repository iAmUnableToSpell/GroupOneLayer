import "../styles/Page.css"
import { useState } from "react";

function Event() {

  const [title, setTitle] = useState("")
  const [desc, setDesc] = useState("")
  const [email, setEmail] = useState("")
  const [date, setDate] = useState(Date.now())
  const [time, setTime] = useState(Date.now())
  const [uuid, setUUID] = useState("")
  

  const onSubmit = (event) => {
    event.preventDefault();
    console.log(event)
  }

  return (
    <div className="wrapper">
        <h1 className="title">Event</h1>
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
            <input type="submit" value={"Create\n Event"} />
          </form>
        </div>
      </div>
  );
}

export default Event;
