import './App.css';
import {BrowserRouter as  Router, Routes, Route } from "react-router-dom";
import HomeComponent from "./HomeComponent/HomeComponent";
import ResultComponent from "./ResultComponent/ResultComponent";
import ResponsiveAppBar from "./HeaderBarComponent/headerBar";
import React, {useState} from "react";
import MealPlanComponent from "./MealPlanComponent/MealPlanComponent";
import {ConfigProvider, Layout} from "antd";
import {Content, Header} from "antd/es/layout/layout";
import {createTheme} from "@mui/material/styles";
import {ThemeProvider} from "@mui/material";
import ResponsiveDrawer from "./SideBarComponent/sideBar";
import './App.css';
import SavedRecipeComponent from "./SavedRecipeComponent/SavedRecipeComponent";
import SearchComponent from "./SearchComponent/SearchComponent";
const muiTheme = createTheme({
    palette: {
        primary: {
            main: '#e67e22',
            dark: '#e67e25',
            contrastText: '#fff',
        }
    },
});

function App() {

    const [drawerVisible, setDrawerVisible] = useState(false);

    const toggleDrawer = () => {
        setDrawerVisible(!drawerVisible);
    };

  return (
      <ConfigProvider
          theme={{
              token: {
                  colorPrimary: "#e67e22",
              },
          }}
      >
          <ThemeProvider theme={muiTheme}>
              <Layout>
                  <Header>
                      <ResponsiveAppBar toggleDrawer={toggleDrawer}></ResponsiveAppBar>
                  </Header>
                  <Layout>
                      <Router>
                          <ResponsiveDrawer visible={drawerVisible} toggleDrawer={toggleDrawer}></ResponsiveDrawer>
                          <Content style={{}}>
                              <Routes>
                                  <Route path={"/"} element={<HomeComponent/>}/>
                                  <Route path={"/result"} element={<ResultComponent/>}/>
                                  <Route path={"/mealplan"} element={<MealPlanComponent/>}/>
                                  <Route path={"/savedrecipes"} element={<SavedRecipeComponent/>}/>
                                  <Route path={"/search"} element={<SearchComponent/>}/>
                              </Routes>
                          </Content>
                      </Router>
                  </Layout>
              </Layout>
          </ThemeProvider>
      </ConfigProvider>
  );
}

export default App;
