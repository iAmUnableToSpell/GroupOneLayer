import "../styles/Page.css"
import { useState } from "react";
import axios from 'axios'
import toast from 'react-hot-toast'

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
        date: date,
        time: time,
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
        toast.error("Invalid Event");
      } else {
        toast.success("Event Created")
      }
    }).catch(error => {
      toast.error("Error connecting to server: " + error);
    })
  }

  return (
    <div className="wrapper">
        <h1 className="title">New Event</h1>
        <div className="input-bar">
          <form onSubmit={onSubmit} className="form">
            <label>
              event date
              <input type="date" value={date} onChange={(e) => setDate(e.target.value)} required/>
            </label>
            <label>
              event time
              <input type="time" value={time} onChange={(e) => setTime(e.target.value)} required/>
            </label>
            <label>
              host email
              <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} 
                pattern = '[a-z0-9._%+\-]+@[a-z0-9.\-]+\.[a-z]{2,}$' required/>
            </label>
            <label>
              event title
              <input type="text" value={title} onChange={(e) => setTitle(e.target.value)} maxlength='255' required/>
            </label>
            <label>
              event description
              <input type="text" value={desc} onChange={(e) => setDesc(e.target.value)} maxlength='600' required/>
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

export default Event;
