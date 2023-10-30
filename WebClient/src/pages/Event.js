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
  const [timestring, setTimestring] = useState('')
  
  const to24h = (timeIn) => {
    let h = timeIn.substring(0,2)
    const m = timeIn.substring(3,5)
    const suf = h > 11 ? 'PM' : 'AM'
    h = h > 11 ? h - 12 : h;
    const res =  h + ':' + m + " " + suf
    setTimestring(res)
  }
  

  const onSubmit = async (event) => {
    event.preventDefault();
    const jsonObject = {
        date: date,
        time: timestring,
        email: email,
        title: title,
        desc: desc,
        uuid: uuid == "" ? null : uuid
    }
    axios({
      method: 'POST',
      url: 'http://ec2-54-145-190-43.compute-1.amazonaws.com:3000/api/event',
      withCredentials : false,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      
      data: JSON.stringify(jsonObject)
      
    })
    .then((response) => {
      if (response.status != 200) {
        alert("Invalid Event");
      } else {
        alert("Event Created")
      }
    }).catch(error => {
      alert("Error connecting to server: " + error);
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
              <input type="time" value={time} onChange={(e) => to24h(e.target.value)} />
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
              <input type="text" value={uuid} onChange={(e) => setUUID(e.target.value)} placeholder='00000000-0000-0000-0000-000000000000'/>
            </label>
            <input type="submit" value={"Create"} />
          </form>
        </div>
      </div>
  );
}

export default Event;
