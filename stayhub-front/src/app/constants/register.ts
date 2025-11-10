export const REGISTER_CONSTANTS = {
    MIN_PASSWORD_LENGTH: 8,
    MIN_AGE: 18,
    PHONE_FORMAT: '+57 seguido de 10 dígitos',
    PASSWORD_REQUIREMENTS: 'Mínimo 8 caracteres, 1 mayúscula y 1 número',
    MAX_LEGAL_DOCUMENTS: 3,
    ROLES: {
    GUEST: {
      label: 'Huésped',
        description: 'Quiero reservar alojamientos',
        icon: '🏠'
    },
    HOST: {
      label: 'Anfitrión',
        description: 'Quiero ofrecer mis propiedades',
        icon: '🔑'
    }
  }
}
