import { colors } from "@/styles/colors";
import styled from "styled-components";

export const ButtonContainer = styled.button<{
  active: boolean;
}>`
  background-color: ${(props) =>
    props.active ? colors.orange900 : colors.gray40};

  &:hover {
    background-color: ${colors.orangeHover};
  }
  color: ${colors.white};
  width: 100%;
  display: flex;
  height: 50px;
  padding: 10px;
  justify-content: center;
  align-items: center;
  gap: 10px;
  align-self: stretch;
  border-radius: 4px;
`;
