import {Component} from "react";

class DialogComponent extends Component {
    handleClick = () => {
        const {predictName, getRecipe} = this.props;
        getRecipe(predictName);
    };
    render() {
        const { onClose, predictName } = this.props;
        return (
            <div style={styles.overlay}>
                <div style={styles.dialog}>
                    <h3>Image Identification</h3>
                    <pre style={styles.pre}>The image you upload is a {predictName}</pre>
                    <button onClick={this.handleClick} style={styles.button}>
                        Yes
                    </button>
                    <button onClick={onClose} style={styles.button}>
                        No
                    </button>
                </div>
            </div>
        );
    }
}
const styles = {
    overlay: {
        position: "fixed",
        top: 0,
        left: 0,
        width: "100%",
        height: "100%",
        backgroundColor: "rgba(0, 0, 0, 0.5)",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
    },
    dialog: {
        backgroundColor: "white",
        padding: "20px",
        borderRadius: "8px",
        width: "400px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.2)",
        textAlign: "center",
    },
    pre: {
        textAlign: "left",
        backgroundColor: "#f4f4f4",
        padding: "10px",
        borderRadius: "5px",
        overflow: "auto",
        maxHeight: "200px",
    },
    button: {
        marginTop: "10px",
        marginRight: "10px",
        padding: "10px 20px",
        fontSize: "16px",
        cursor: "pointer",
        backgroundColor: "#007bff",
        color: "white",
        border: "none",
        borderRadius: "5px",
    },
};
export default DialogComponent;