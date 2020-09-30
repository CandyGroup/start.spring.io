import PropTypes from 'prop-types'
import React from 'react'

const FieldTextArea = ({id, text, value, onChange, disabled, inputRef}) => (
    <div className='control control-inline'>
        <label htmlFor={id}>{text}</label>
        <textarea
            id={id}
            rows='10'
            className='input'
            disabled={disabled}
            value={value}
            onChange={onChange}
            ref={inputRef}
        />
    </div>
)

FieldTextArea.defaultProps = {
    disabled: false,
    inputRef: null,
}

FieldTextArea.propTypes = {
    id: PropTypes.string.isRequired,
    text: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired,
    inputRef: PropTypes.oneOfType([
        PropTypes.func,
        PropTypes.shape({current: PropTypes.instanceOf(Element)}),
    ]),
    disabled: PropTypes.bool,
}

export default FieldTextArea
