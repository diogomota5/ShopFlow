export interface AddressesResponse {
  id: string;
  nickname: string | null;
  zipCode: string;
  street: string;
  streetNumber: string;
  complement: string | null;
  neighborhood: string;
  city: string;
  state: string;
  primaryAddress: boolean;
}

export interface AddressesRequest {
  nickname?: string;
  zipCode: string;
  street: string;
  streetNumber: string;
  complement?: string;
  neighborhood: string;
  city: string;
  state: string;
  primaryAddress: boolean;
}
