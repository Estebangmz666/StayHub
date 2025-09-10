# StayHub - Component System

**Note**: This document defines the component system for StayHub's user interface, based on Atomic Design principles to ensure modularity, reusability, and consistency. The system aligns with the color palette (color-palette.md), typography, and requirements defined in the Business Rules (RN-XXX) and Non-Functional Requirements (RNF-XXX). The interface uses Spanish for all user-facing elements (RNF-028), with code and documentation in English for technical consistency.

## Atomic Design Overview
StayHub's component system is structured using Atomic Design, which organizes UI elements into five levels: Atoms, Molecules, Organisms, Templates, and Pages. This ensures a scalable, maintainable, and elegant interface that reflects StayHub's design principles of elegance, minimalism, professionalism, and functionality.

## Atoms
Atoms are the smallest building blocks of the UI, designed to be reusable and consistent with the color palette and typography. Each atom includes a visual preview using HTML/CSS for clarity.

| Component  | Description                          | Preview                                                                                                                                                                                                                                                   | Properties                                                                                                                                                                                                                  | Related Rules/Requirements                      |
|------------|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------|
| Button     | Interactive button for user actions. | <div style="background-color: #1E88E5; color: #FFFFFF; padding: 8px 16px; border-radius: 4px; display: inline-block; text-align: center; font-family: 'San Francisco Pro', 'Inter', sans-serif; font-size: 16px; font-weight: bold;">Reservar ahora</div> | - **Color**: Elegant Blue (#1E88E5) for primary actions, Light Gray (#F5F5F5) for secondary.<br>- **Text**: San Francisco Pro, 16px, bold.<br>- **Size**: Minimum 44px (RNF-027).<br>- **States**: Normal, hover, disabled. | RN-017 ("Reservar ahora"), RNF-019, RNF-027     |
| Text Input | Input field for forms.               | <div style="border: 1px solid #A0A0A0; padding: 8px; border-radius: 4px; display: inline-block; font-family: 'San Francisco Pro', 'Inter', sans-serif; font-size: 16px; color: #333333; width: 150px;">Ciudad</div>                                       | - **Border**: Medium Gray (#A0A0A0).<br>- **Text**: Dark Gray (#333333), 16px.<br>- **Placeholder**: Medium Gray (#A0A0A0), 14px.<br>- **Error State**: Border in Error Red (#D32F2F).                                      | RN-001 (email validation), RNF-020, RNF-025     |
| Label      | Text label for content.              | <span style="font-family: 'San Francisco Pro', 'Inter', sans-serif; font-size: 16px; color: #333333;">Título del alojamiento</span>                                                                                                                       | - **Color**: Dark Gray (#333333) for primary, Medium Gray (#A0A0A0) for secondary.<br>- **Size**: 16px (primary), 14px (secondary).                                                                                         | RN-042 (accommodation description), RNF-039     |
| Icon       | Icon for actions or states.          | <span style="font-size: 24px; color: #1E88E5;">🔍</span>                                                                                                                                                                                                  | - **Color**: Dark Gray (#333333) or Elegant Blue (#1E88E5) for interactive.<br>- **Size**: 24px.                                                                                                                            | RN-010 (search icon), RNF-023                   |
| Image      | Optimized image for galleries.       | <div style="background-color: #F5F5F5; width: 100px; height: 60px; display: inline-block; text-align: center; line-height: 60px; font-family: 'San Francisco Pro', 'Inter', sans-serif; font-size: 14px; color: #333333;">[Imagen]</div>                  | - **Progressive Loading**: <5s (RNF-003).<br>- **Size**: Responsive to screen (RNF-018).                                                                                                                                    | RN-038 (accommodation images), RNF-003, RNF-023 |

## Molecules
Molecules combine atoms to form functional units.

| Component        | Description                             | Included Atoms                                                                                                                                  | Related Rules/Requirements       |
|------------------|-----------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------|
| Search Form      | Form for searching accommodations.      | - Text Input (city, dates).<br>- Button (search).<br>- Icon (magnifying glass).                                                                 | UC-004, RN-010, RNF-020, RNF-021 |
| Reservation Card | Card displaying basic reservation info. | - Label (dates, price).<br>- Button (cancel, details).<br>- Icon (status).                                                                      | UC-006, RN-017, RN-022, RNF-025  |
| Notification     | In-app alert or message.                | - Label (message).<br>- Icon (success, error, warning).<br>- **Color**: Success Green (#28A745), Error Red (#D32F2F), Warning Yellow (#FBC02D). | RN-030, RNF-026, RNF-025         |

## Organisms
Organisms are complete UI sections combining molecules and atoms.

| Component          | Description                                  | Included Molecules/Atoms                                                                        | Related Rules/Requirements       |
|--------------------|----------------------------------------------|-------------------------------------------------------------------------------------------------|----------------------------------|
| Navigation Bar     | Top navigation bar.                          | - Button (home, profile, logout).<br>- Icon (hamburger menu for mobile).<br>- Label (username). | UC-001, UC-002, RNF-019, RNF-027 |
| Accommodation Card | Card for an accommodation in search results. | - Image (main photo).<br>- Label (title, price, rating).<br>- Button (view details).            | UC-004, RN-007, RN-037, RNF-023  |
| Filter Sidebar     | Sidebar for search filters.                  | - Search Form.<br>- Text Input (price, accommodation type).<br>- Button (apply filters).        | UC-004, RN-010, RNF-021          |

## Templates
Templates define the layout of pages by combining organisms.

| Template                       | Description                                | Included Organisms                                                                                                                          | Related Rules/Requirements               |
|--------------------------------|--------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------|
| Search Results Template        | Layout for the search results page.        | - Navigation Bar.<br>- Filter Sidebar.<br>- Accommodation Card (list).<br>- Pagination (atom).                                              | UC-004, RN-010, RNF-005, RNF-021         |
| Accommodation Details Template | Layout for the accommodation details page. | - Navigation Bar.<br>- Image (gallery).<br>- Label (title, description, price).<br>- Reservation Form (molecule).<br>- Calendar (molecule). | UC-005, RN-042, RN-038, RNF-022, RNF-023 |

## Pages
Pages are specific instances of templates with real content.

| Page                       | Description                                                             | Base Template                  | Related Rules/Requirements               |
|----------------------------|-------------------------------------------------------------------------|--------------------------------|------------------------------------------|
| Search Results Page        | Displays search results for accommodations in a city (e.g., Medellín).  | Search Results Template        | UC-004, RN-007, RN-010, RNF-001, RNF-021 |
| Accommodation Details Page | Shows details of a specific accommodation (e.g., "Casa en El Poblado"). | Accommodation Details Template | UC-005, RN-037, RN-042, RNF-003, RNF-022 |

## Implementation Notes
- **Frontend (Angular)**: Implement components as Angular components, using CSS/SCSS variables from the color palette (e.g., `--accent-blue: #1E88E5;`) for consistency (RNF-037).
- **Accessibility**: Ensure all components meet WCAG 2.1 Level AA, with proper ARIA labels, keyboard navigation, and sufficient contrast (RNF-039).
- **Responsive Design**: Components must adapt to screen sizes from 320px to 1920px, maintaining functionality and legibility (RNF-018).
- **Consistency**: Use Spanish for all user-facing text, following the defined error message formats and terminology (RNF-028, RN-001, RN-012).
- **Testing**: Use tools like Storybook to preview and test components in isolation, ensuring modularity and reusability.
