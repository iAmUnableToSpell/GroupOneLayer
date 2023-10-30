import { useState } from 'react'
import Navigator from './components/Navigator';
import Event from "./pages/Event";
import { Toaster } from 'react-hot-toast';


function App() {

    const [page, setPage] = useState(<Event/>);

    return (
        <div className="App" style={{'margin': '0'}}>
            <Toaster / >
            <Navigator setPage={setPage} / >
            {page}
        </div>
    );
  }
  
  export default App;