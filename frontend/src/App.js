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
import SavedRecipeComponent from "./SavedRecipeComponent/SavedRecipeComponent";
import SearchComponent from "./SearchComponent/SearchComponent";
import LoginComponent from "./LoginComponent/LoginComponent";
import SignUpComponent from "./SignUpComponent/SignUpComponent";
import {AuthProvider} from "./AuthContext";
import ProfileComponent from "./ProfileComponent/ProfileComponent";
const muiTheme = createTheme({
    palette: {
        primary: {
            main: '#e67e22',
            dark: '#e67e25',
            contrastText: '#fff',
        }
    },
    components: {
        MuiLink: {
            styleOverrides: {
                root: {
                    color: "black",
                    textDecorationColor: "black",
                    "&:hover": {
                        color: "#e67e22",
                    },
                },
            },
        }
    }
});

function App() {

    const [drawerVisible, setDrawerVisible] = useState(false);

    const toggleDrawer = () => {
        setDrawerVisible(!drawerVisible);
    };

  return (
      <AuthProvider>
          <Router>
              <ConfigProvider
                  theme={{
                      token: {
                          colorPrimary: "#e67e22",
                      },
                  }}
              >
                  <ThemeProvider theme={muiTheme}>
                      <Layout style={{height:"100vh"}}>
                          <Header>
                              <ResponsiveAppBar toggleDrawer={toggleDrawer}></ResponsiveAppBar>
                          </Header>
                          <Layout>
                                  <ResponsiveDrawer visible={drawerVisible} toggleDrawer={toggleDrawer}></ResponsiveDrawer>
                                  <Content style={{overflow:"auto"}}>
                                      <Routes>
                                          <Route path={"/"} element={<HomeComponent/>}/>
                                          <Route path={"/result"} element={<ResultComponent/>}/>
                                          <Route path={"/mealplan"} element={<MealPlanComponent/>}/>
                                          <Route path={"/savedrecipes"} element={<SavedRecipeComponent/>}/>
                                          <Route path={"/search"} element={<SearchComponent/>}/>
                                          <Route path={"/login"} element={<LoginComponent/>}/>
                                          <Route path={"*"} element={<HomeComponent/>}/>
                                          <Route path={"/signup"} element={<SignUpComponent/>}/>
                                          <Route path={"/profile"} element={<ProfileComponent/>}/>
                                      </Routes>
                                  </Content>
                          </Layout>
                      </Layout>
                  </ThemeProvider>
              </ConfigProvider>
          </Router>
      </AuthProvider>
  );
}

export default App;
