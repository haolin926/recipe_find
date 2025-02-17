import * as React from 'react';
import {useLocation, useNavigate} from "react-router-dom";
import {Drawer, Menu} from "antd";
import "./sideBar.css"

function ResponsiveDrawer({visible, toggleDrawer}) {
    const navigate = useNavigate();
    const location = useLocation();

    const handleMenuClick = (key) => {
        navigate(key);
        toggleDrawer(); // Close the drawer after navigation
    };

    return (
        <Drawer title="Menu" placement="left" onClose={toggleDrawer} open={visible}
                variant="temporary">
            <Menu
                mode="inline"
                onClick={({ key }) => handleMenuClick(key)}
                selectedKeys={[location.pathname]}
                items={[
                    { key: "/", label: "Home" },
                    { key: "/mealplan", label: "Meal Plan" },
                    { key: "/savedrecipes", label: "Saved Recipes" }
                ]}
            />
        </Drawer>
    );
}

export default ResponsiveDrawer;