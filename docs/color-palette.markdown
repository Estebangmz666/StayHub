# Color Palette - StayHub

**Note**: This document defines the color palette and typography for StayHub's user interface, designed to reflect elegance, minimalism, professionalism, and functionality. The palette ensures accessibility per WCAG 2.1 Level AA (RNF-039) and aligns with the usability requirements (RNF-018, RNF-019, RNF-020, RNF-025). The interface uses Spanish for all user-facing elements (RNF-028), with code and documentation in English for technical consistency.

## Color Palette

### Primary Colors (Monochromatic Base)
The primary colors form a monochromatic base to ensure a clean, minimalistic, and professional look, aligning with StayHub's design principles.

| Color Name       | Hex Code | Preview | Usage Example                                                                 |
|------------------|----------|---------|------------------------------------------------------------------------------|
| Pure White       | #FFFFFF  | <span style="background-color: #FFFFFF; width: 20px; height: 20px; display: inline-block; border: 1px solid #000;"></span> | Main background, cards, content areas (e.g., accommodation details, RN-007). |
| Light Gray       | #F5F5F5  | <span style="background-color: #F5F5F5; width: 20px; height: 20px; display: inline-block; border: 1px solid #000;"></span> | Secondary backgrounds, dividers, subtle emphasis (e.g., search filters, RN-010). |
| Medium Gray      | #A0A0A0  | <span style="background-color: #A0A0A0; width: 20px; height: 20px; display: inline-block; border: 1px solid #000;"></span> | Secondary text, borders, non-interactive icons (e.g., reservation history, RN-022). |
| Dark Gray        | #333333  | <span style="background-color: #333333; width: 20px; height: 20px; display: inline-block; border: 1px solid #000;"></span> | Primary text, headings, high-contrast elements (e.g., accommodation titles, RN-037). |
| Pure Black       | #000000  | <span style="background-color: #000000; width: 20px; height: 20px; display: inline-block; border: 1px solid #000;"></span> | Specific details, soft shadows, or button/icon emphasis (e.g., image gallery, RN-038). |

### Accent Color
The accent color is used for interactive elements to guide user actions while maintaining elegance.

| Color Name       | Hex Code | Preview | Usage Example                                                                 |
|------------------|----------|---------|------------------------------------------------------------------------------|
| Elegant Blue     | #1E88E5  | <span style="background-color: #1E88E5; width: 20px; height: 20px; display: inline-block; border: 1px solid #000;"></span> | Primary buttons, links, interactive elements (e.g., "Reservar ahora" button, RN-017). |

### Support Colors (States)
Support colors indicate system states and align with user feedback requirements (RNF-025).

| Color Name       | Hex Code | Preview | Usage Example                                                                 |
|------------------|----------|---------|------------------------------------------------------------------------------|
| Success Green    | #28A745  | <span style="background-color: #28A745; width: 20px; height: 20px; display: inline-block; border: 1px solid #000;"></span> | Success indicators (e.g., reservation confirmation, RN-017).                  |
| Error Red        | #D32F2F  | <span style="background-color: #D32F2F; width: 20px; height: 20px; display: inline-block; border: 1px solid #000;"></span> | Error messages (e.g., "Correo electrónico ya está en uso," RN-001).          |
| Warning Yellow   | #FBC02D  | <span style="background-color: #FBC02D; width: 20px; height: 20px; display: inline-block; border: 1px solid #000;"></span> | Alerts or notifications (e.g., reminder for upcoming check-in, RN-030).       |

### Accessibility
- All color combinations meet WCAG 2.1 Level AA contrast ratios (minimum 4.5:1 for normal text, 3:1 for large text).
- Example: Dark Gray (#333333) on Pure White (#FFFFFF) has a contrast ratio of 12.6:1, ensuring readability (RNF-039).

## Typography
The typography uses a modern sans-serif font to align with StayHub's minimalistic and professional aesthetic, inspired by iPhone's clean design.

- **Primary Font**: San Francisco Pro (or Inter as a free alternative).
- **Typography Hierarchy**:
  - **Headings (H1)**: 24px, bold, Dark Gray (#333333) – Used for page titles (e.g., accommodation name, RN-037).
  - **Subheadings (H2)**: 18px, medium, Dark Gray (#333333) – Used for section titles (e.g., "Detalles de la reserva," RN-017).
  - **Body Text**: 16px, regular, Dark Gray (#333333) – Used for main content (e.g., accommodation description, RN-042).
  - **Secondary Text**: 14px, regular, Medium Gray (#A0A0A0) – Used for secondary information (e.g., reservation dates, RN-022).
- **Accessibility**: Font sizes and weights comply with WCAG 2.1 Level AA for readability, supporting screen readers and keyboard navigation (RNF-039).

## Usage Guidelines
- **Primary Colors**: Use Pure White (#FFFFFF) and Light Gray (#F5F5F5) for backgrounds to maintain a clean, minimalistic interface. Dark Gray (#333333) is the default for text to ensure high contrast (RNF-018, RNF-025).
- **Accent Color**: Apply Elegant Blue (#1E88E5) sparingly for interactive elements like buttons and links to guide user actions (RNF-019, RNF-021).
- **Support Colors**: Use Success Green (#28A745), Error Red (#D32F2F), and Warning Yellow (#FBC02D) for feedback states in forms, notifications, and alerts (RNF-020, RNF-025, RNF-026).
- **Consistency**: Ensure all UI elements use Spanish with proper grammar and terminology (RNF-028), with error messages following the formats defined in the business rules (e.g., RN-001, RN-012).
- **Responsive Design**: Colors and typography must adapt to screen sizes from 320px to 1920px, maintaining legibility and functionality (RNF-018).

## Implementation Notes
- **Frontend (Angular)**: Define the color palette in CSS/SCSS variables (e.g., `--primary-white: #FFFFFF; --accent-blue: #1E88E5;`) for consistent use across components (RNF-037).
- **Accessibility Testing**: Use tools like WAVE or Lighthouse to verify WCAG 2.1 Level AA compliance for color contrast and typography (RNF-039).
- **External Services**: Ensure image galleries (RN-038) and Mapbox maps (RN-080) use the defined palette for visual consistency (RNF-004, RNF-023).