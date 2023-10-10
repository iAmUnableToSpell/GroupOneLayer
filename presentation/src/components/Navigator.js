import "../styles/Navigator.css"



import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEllipsis, faUser, faCalendar } from '@fortawesome/free-solid-svg-icons';
import Participant from "../pages/Participant";
import Event from "../pages/Event";

function Navigator({setPage}) {

    const onEventClicked = () => {
        setPage(<Event/>)
        console.log('event')
    }

    const onParticipantClicked = () => {
        setPage(<Participant/>)
    }

  return (
    <div class='nav'>
        <div class='nav-button'> 
            <i class='nav-label'><FontAwesomeIcon class='fa icon' icon={faEllipsis}/></i>
            <span class='nav-container'>
                <i class="button" onClick={onEventClicked}><FontAwesomeIcon class='fa nav-icon icon' icon={faCalendar} size ="3x"/></i>
                <i class="button" onClick={onParticipantClicked}><FontAwesomeIcon class='fa nav-icon icon' icon={faUser} size ="3x"/></i>
                <i class="button"><FontAwesomeIcon class=''/></i>
            </span>
        </div>
    </div>
  );
}

export default Navigator;
