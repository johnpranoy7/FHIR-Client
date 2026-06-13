<template>
  <svg
    class="fhir-logo"
    viewBox="0 0 32 32"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
  >
    <defs>
      <linearGradient id="headerFhirGrad" x1="4" y1="28" x2="28" y2="4" gradientUnits="userSpaceOnUse">
        <stop stop-color="#0891b2" />
        <stop offset="1" stop-color="#6366f1" />
      </linearGradient>
    </defs>
    <path fill="url(#headerFhirGrad)" d="M5 22.5 16 27l11-4.5-11-4.5-11 4.5Z" />
    <path fill="url(#headerFhirGrad)" fill-opacity="0.88" d="M7.5 16.5 16 21l8.5-4.5L16 12l-8.5 4.5Z" />
    <path fill="url(#headerFhirGrad)" fill-opacity="0.76" d="M10 10.5 16 14l6-3.5L16 7l-6 3.5Z" />
    <circle cx="16" cy="8" r="1.5" fill="#e0f2fe" />
  </svg>
</template>

<style scoped>
.fhir-logo {
  width: 26px;
  height: 26px;
  display: block;
}
</style>
