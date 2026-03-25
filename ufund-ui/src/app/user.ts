import { Basket } from "./basket";

export enum Usertype {
  ADMIN  = 'ADMIN',
  HELPER = 'HELPER',
  SUPER = 'SUPER',
}

export interface loginrequest {
    username: string,
    password: string,
}

export interface User {
    name: string,
    password: string,
    type: Usertype
}

export interface Helper extends User {
    basket: Basket,
    contributedNeeds: number
}