import "../styles/Page.css"
import axios from "axios"
import { useEffect, useState } from "react";

function GetEvents() {

  const [events, setEvents] = useState([]);

  useEffect(() => {
    updateEvents();
  }, [])

  const updateEvents = async () => {
    axios({
      method: 'GET',
      url: 'http://ec2-54-145-190-43.compute-1.amazonaws.com:6969/api/list-events',
      headers : {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      withCredentials : false
    })
    .then((response) => {
      console.log(response)
      setEvents(response.data.events)
    }).catch(error => {
      console.log(error);
    })
  }

  const getEventsList = () => {
    return(events.map(event => {
    return(
      <div className="list-entry">  
        <div className="list-data">{event.title}</div>
        <div className="list-data">{event.desc}</div>
        <div className="list-data">{event.date}</div>
        <div className="list-data">{event.time}</div>
        <div className="list-data">{event.email}</div>
        <div className="list-data">{event.uuid}</div>
      </div>
    )}))
  }

  const getLegend = () => {
    if (events.length > 0) {
    return (
    <div className="legend">
      <div className="list-data">Title</div>
      <div className="list-data">Description</div>
      <div className="list-data">Date</div>
      <div className="list-data">Time</div>
      <div className="list-data">Email</div>
      <div className="list-data-wide">UUID</div>
    </div>
    )} else return <p>No Data To Display</p> 
  }

  return (
    <div className="wrapper">
        <h1 className="title">List Events</h1>
        <div className="input-bar">
          <div className="list-container">
            {getLegend()}
            {getEventsList()}
          </div>
        </div>
      </div>
  );
}

export default GetEvents;
