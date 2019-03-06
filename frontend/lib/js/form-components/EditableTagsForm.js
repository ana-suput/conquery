// @flow

import React from "react";
import styled from "@emotion/styled";
import T from "i18n-react";
import clickOutside from "react-onclickoutside";

// A multi-select where new items can be created
import { Creatable as Select } from "react-select";

import PrimaryButton from "../button/PrimaryButton";

type PropsType = {
  className?: string,
  tags?: string[],
  loading: boolean,
  onSubmit: () => void,
  onCancel: () => void,
  availableTags?: string[]
};

const StyledPrimaryButton = styled(PrimaryButton)`
  margin-top: 5px;
`;

class EditableTagsForm extends React.Component<PropsType> {
  constructor(props: PropsType) {
    super(props);
    this.state = {
      values:
        (props.tags && props.tags.map(t => ({ label: t, value: t }))) || []
    };
  }

  handleClickOutside() {
    this.props.onCancel();
  }

  handleChange = (values: any, actionMeta: any) => {
    this.setState({ values });
  };

  _onSubmit(e) {
    e.preventDefault();

    this.props.onSubmit(this.state.values.map(v => v.value));
  }

  render() {
    return (
      <form
        className={this.props.className}
        onSubmit={this._onSubmit.bind(this)}
      >
        <Select
          name="input"
          value={this.state.values}
          options={this.props.availableTags.map(t => ({ label: t, value: t }))}
          onChange={this.handleChange}
          isMulti
          isClearable
          autoFocus={true}
          placeholder={T.translate("reactSelect.tagPlaceholder")}
          noOptionsMessage={() => T.translate("reactSelect.noResults")}
        />
        <StyledPrimaryButton type="submit" small disabled={this.props.loading}>
          {T.translate("common.save")}
        </StyledPrimaryButton>
      </form>
    );
  }
}

export default clickOutside(EditableTagsForm);
