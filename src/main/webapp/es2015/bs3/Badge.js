import React from "react";

export default class Counter extends React.Component {
    constructor(props) {
      super(props);
    }
    render() {
        if (this.props.counter > 0) {
            return (<span className="badge">{this.props.counter}</span>)
        } else {
            return null;
        }
    }
}