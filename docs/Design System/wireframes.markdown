#  StayHub - Wireframes

**Note**: This document describes low-fidelity wireframes for StayHub's **key pages**, focusing on layout and functionality without final visual details (e.g., colors, typography). The wireframes are based on the component system (component-system.md) using Atomic Design, aligning with the Business Rules (RN-XXX) and Non-Functional Requirements (RNF-XXX). The interface uses Spanish for user-facing elements (RNF-028), with documentation in English for technical consistency.

## Overview
Low-fidelity wireframes outline the structure and user flow for StayHub's key pages: the Search Results Page and the Accommodation Details Page. They use placeholders (e.g., rectangles, text) and ASCII diagrams to represent components from the component system (atoms, molecules, organisms) and ensure usability (RNF-019), accessibility (RNF-039), and responsiveness (RNF-018).

## Search Results Page Wireframe

**Purpose**: Displays a list of accommodations based on user filters (city, dates, price, etc.), supporting intuitive navigation and filtering.

**ASCII Diagram**:
```
+-------------------------------+
| [Inicio] [Perfil] [Cerrar]    |  <- Navigation Bar
+-------------------------------+
| [Ciudad: ____] [Buscar]       |  <- Filter Sidebar
| [Fechas: ____]                |
| [Precio: ____] [Aplicar]      |
+-------------------------------+
| [Imagen] Título    $Precio    |  <- Accommodation Card
| [Imagen] Título    $Precio    |
| [Imagen] Título    $Precio    |
|        [< 1 2 3 >]            |  <- Pagination
+-------------------------------+
```

**Layout**:
```
| Section | Components | Description | Related Rules/Requirements |
|---------|------------|-------------|----------------------------|
| Header | Navigation Bar (Organism) | - Top bar with buttons: "Inicio", "Perfil", "Cerrar sesión".<br>- Hamburger menu icon for mobile (RNF-027).<br>- Username label (right-aligned). | UC-001, UC-002, RN-002, RNF-019, RNF-027 |
| Sidebar (Left) | Filter Sidebar (Organism) | - Search Form (Molecule): Text Inputs for city, dates, and price range; Button ("Buscar").<br>- Additional filters: Checkboxes for accommodation type, amenities.<br>- Button ("Aplicar filtros"). | UC-004, RN-010, RNF-020, RNF-021 |
| Main Content | Accommodation Card (Organism, List) | - Grid or list of cards, each with:<br>  - Image placeholder (rectangle, 200x120px).<br>  - Labels: Title, price, rating.<br>  - Button ("Ver detalles"). | UC-004, RN-007, RN-037, RNF-023 |
| Footer | Pagination (Atom) | - Buttons for page navigation (e.g., "<", "1", "2", ">").<br>- Centered or right-aligned. | RN-010, RNF-005 |
```

**Notes**:
- **Responsive Design**: Sidebar collapses into a dropdown on mobile screens (<768px, RNF-018).
- **Performance**: Search results load in <2s (RNF-001), pagination in <1s (RNF-005).
- **Accessibility**: Filter inputs and buttons support keyboard navigation and ARIA labels (RNF-039).
- **User Flow**: Users enter search criteria, apply filters, and click "Ver detalles" to view an accommodation (UC-004).

## Accommodation Details Page Wireframe

**Purpose**: Displays detailed information for a specific accommodation, including images, description, and reservation options.

**ASCII Diagram**:
```
+-------------------------------+
| [Inicio] [Perfil] [Cerrar]    |  <- Navigation Bar
+-------------------------------+
| [Imagen Principal]            |  <- Image Gallery
| [Img1] [Img2] [Img3] [Img4]  |
+-------------------------------+
| Título del Alojamiento        |  <- Labels
| Descripción...                |
| $Precio   ★ Calificación      |
+-------------------------------+
| [Fecha Entrada: ____]         |  <- Reservation Form
| [Fecha Salida: ____]          |
| [Huéspedes: ____] [Reservar]  |
+-------------------------------+
| [Calendario: Mes/Días]        |  <- Calendar
+-------------------------------+
```

**Layout**:
```
| Section | Components | Description | Related Rules/Requirements |
|---------|------------|-------------|----------------------------|
| Header | Navigation Bar (Organism) | - Same as Search Results Page: Buttons ("Inicio", "Perfil", "Cerrar sesión"), hamburger menu icon, username label. | UC-001, UC-002, RN-002, RNF-019, RNF-027 |
| Main Content (Top) | Image Gallery (Molecule) | - Main image placeholder (rectangle, 600x400px).<br>- Thumbnail placeholders below (4 rectangles, 100x60px). | RN-038, RNF-003, RNF-023 |
| Main Content (Middle) | Labels (Atom) | - Title (e.g., "Casa en El Poblado").<br>- Description (paragraph placeholder).<br>- Price, rating, and availability labels. | RN-042, RN-037, RNF-039 |
| Main Content (Right) | Reservation Form (Molecule) | - Text Inputs: Check-in date, check-out date, number of guests.<br>- Button ("Reservar ahora"). | RN-017, RNF-020, RNF-025 |
| Main Content (Bottom) | Calendar (Molecule) | - Grid representing a month with selectable dates.<br>- Visual indicators for available/booked dates. | RN-016, RNF-022 |
```
**Notes**:
- **Responsive Design**: Image gallery stacks vertically on mobile; form and calendar adjust to fit screen (RNF-018).
- **Performance**: Images load in <5s with progressive loading (RNF-003).
- **Accessibility**: Calendar and form support keyboard navigation and screen readers (RNF-039).
- **User Flow**: Users view accommodation details, select dates via the calendar, fill the reservation form, and click "Reservar ahora" (UC-005, RN-017).