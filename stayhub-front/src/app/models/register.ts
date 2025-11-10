export enum Role {
  GUEST = 'GUEST',
  HOST = 'HOST',
}

export interface UserRegistrationDTO {
  email: string;
  password: string;
  name: string;
  phoneNumber: string;
  birthDate: string;
  role: Role;

  //Host Profile optional fields
  profilePicture?: string;
  description?: string;
  legalDocuments?: string[];
}

export interface RegisterFormData {
  email: string;
  password: string;
  confirmPassword: string;
  name: string;
  phoneNumber: string;
  birthDate: string;
  role: Role;

  //Host Profile optional fields
  profilePicture?: string;
  description?: string;
  legalDocument1?: string;
  legalDocument2?: string;
  legalDocument3?: string;
}

export interface UserResponseDTO {
  id: number;
  email: string;
  name: string;
  phoneNumber: string;
  birthDate: string;
  role: Role;
  profilePicture?: string;
  description?: string;
  legalDocuments?: string[];
}

export interface RegisterValidationErrors {
  email?: string;
  password?: string;
  confirmPassword?: string;
  name?: string;
  phoneNumber?: string;
  birthDate?: string;
  profilePicture?: string;
  description?: string;
  legalDocument1?: string;
  legalDocument2?: string;
  legalDocument3?: string;
}
