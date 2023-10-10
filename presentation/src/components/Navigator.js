import "../styles/Navigator.css"



import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEllipsis, faUser, faCalendar, faList, faUsers } from '@fortawesome/free-solid-svg-icons';
import Participant from "../pages/Participant";
import Event from "../pages/Event";
import GetEvents from "../pages/GetEvents";
import GetParticipants from "../pages/GetParticipants";

function Navigator({setPage}) {

    const onEventClicked = () => {
        setPage(<Event/>)
    }

    const onParticipantClicked = () => {
        setPage(<Participant/>)
    }

    const onGetParticipantsClicked = () => {
        console.log('test')
        setPage(<GetParticipants/>)
    }

    const onGetEventsClicked = () => {
        setPage(<GetEvents/>)
    }

  return (
    <div class='nav'>
        <div class='nav-button'> 
            <i class='nav-label'><FontAwesomeIcon class='fa icon' icon={faEllipsis}/></i>
            <span class='nav-container'>
                <i class="button" onClick={onEventClicked}><FontAwesomeIcon class='fa nav-icon icon' icon={faCalendar} size ="3x"/></i>
                <i class="button" onClick={onParticipantClicked}><FontAwesomeIcon class='fa nav-icon icon' icon={faUser} size ="3x"/></i>
                <i class="button" onClick={onGetEventsClicked}><FontAwesomeIcon class='fa nav-icon icon' icon={faList} size ="3x"/></i>
                <i class="button" onClick={onGetParticipantsClicked}><FontAwesomeIcon class='fa nav-icon icon' icon={faUsers} size ="3x"/></i>
                <i class="button"></i>
            </span>
        </div>
    </div>
  );
}

export default Navigator;
