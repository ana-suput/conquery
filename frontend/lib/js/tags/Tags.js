// @flow

import React                from 'react';
import Tag                  from './Tag';

type PropsType = {
  className: string,
  tags: {
    label: string,
    isSelected: boolean,
  }[],
  onClickTag: (string) => void,
};

export const Tags = (props: PropsType) => {
  return !!props.tags && props.tags.length > 0 && (
    <div className={props.className}>
      {
        props.tags.map((tag, i) => (
          <Tag
            key={i}
            label={tag.label}
            isSelected={tag.isSelected}
            onClick={() => props.onClickTag(tag.label)}
          />
        ))
      }
    </div>
  );
};


export default Tags;
