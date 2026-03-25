
export enum Needtype {
  MONETARY  = 'Monetary',
  VOLUNTEER = 'Volunteer',
  RESOURCE  = 'Resource'
}

export interface Need {
    id: number,
    name: string,
    description: string,
    progress: number,
    goal: number,
    'need-type': Needtype,
    listed: boolean;
}
