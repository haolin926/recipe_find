import './App.css';
import {BrowserRouter as  Router, Routes, Route } from "react-router-dom";
import HomeComponent from "./HomeComponent/HomeComponent";
import ResultComponent from "./ResultComponent/ResultComponent";
function App() {
  return (
    <div className="App">
      <Router>
        <Routes>
            <Route path="/" element={<HomeComponent/>}/>
            <Route path={"/result"} element={<ResultComponent/>}/>
        </Routes>
      </Router>
    </div>
  );
}

export default App;
