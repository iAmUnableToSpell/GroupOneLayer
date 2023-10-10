import "../styles/Page.css"
import axios from "axios"
import { useEffect, useState } from "react";

function GetEvents() {

  const [events, setEvents] = useState([]);

  useEffect(() => {
    updateEvents();
  }, [])

  const updateEvents = async () => {
    // axios.get("localhost:5000/get-events").then((response) => {
    //   setEvents(response)
    // })
    // temp dummy events for testing
    setEvents([{
      date: '21-02-2020',
      time: '10:32 PM',
      email: 'email',
      title: 'title1',
      desc: 'desc',
      uuid: '550e8400-e29b-41d4-a716-446655440001'
    },{
      date: '21-02-2020',
      time: '10:32 PM',
      email: 'email',
      title: 'title2',
      desc: 'desc',
      uuid: '550e8400-e29b-41d4-a716-446655440002'
    },{
      date: '21-02-2020',
      time: '10:32 PM',
      email: 'email',
      title: 'title3',
      desc: 'desc',
      uuid: '550e8400-e29b-41d4-a716-446655440003'
    }])
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

  return (
    <div className="wrapper">
        <h1 className="title">List Events</h1>
        <div className="input-bar">
          <div className="list-container">
            <div className="legend">
              <div className="list-data">Title</div>
              <div className="list-data">Description</div>
              <div className="list-data">Date</div>
              <div className="list-data">Time</div>
              <div className="list-data">Email</div>
              <div className="list-data">UUID</div>
            </div>
            {getEventsList()}
          </div>
        </div>
      </div>
  );
}

export default GetEvents;
