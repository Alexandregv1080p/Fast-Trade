export enum UserRole { ADMIN = 'ADMIN', USER = 'USER', COLLABORATOR = 'COLLABORATOR' }

export interface Address {
  id: string | number;
  zipCode?: string;
  street: string;
  number: string;
  complement?: string;
  neighborhood?: string;
  city: string;
  state: string;
  country?: string;
  isDefault?: boolean;
}

export interface User {
  id: string;
  email: string;
  name: string;
  cpfCnpj?: string;
  phone?: string;
  role: UserRole | string;
  address?: Address | null;
  addresses?: Address[];
  createdAt?: string;
  updatedAt?: string;
  avatar?: string;
  imageUrl?: string;
  birthDate?: string;
  gender?: string;
  profession?: string;
  providerType?: string;
  trades?: number;
  score?: number;
  status?: string;
  blocked?: boolean;
}

export interface AuthSession { user: User; token: string; expiresAt: Date; }
export interface LoginCredentials { email: string; password: string; }
export interface RegisterData { email: string; password: string; name: string; cpfCnpj?: string; phone?: string; }
