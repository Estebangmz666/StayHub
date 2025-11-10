export interface PasswordResetRequestDTO {
  email: string;
}

export interface ResetPasswordDTO {
  token: string;
  newPassword: string;
}

export interface SuccessResponseDTO {
  message: string;
}

export interface ForgotPasswordErrors {
  email?: string;
}

export interface ResetPasswordErrors {
  token?: string;
  newPassword?: string;
  confirmPassword?: string;
}
