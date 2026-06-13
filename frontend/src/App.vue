<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ArrowLeft, InfoFilled } from '@element-plus/icons-vue'
import DocumentList from './components/DocumentList.vue'
import LeftNav from './components/LeftNav.vue'
import PatientSearch from './components/PatientSearch.vue'

const selectedPatient = ref(null)
const searchExpanded = ref(true)
const navOpen = ref(false)
const isCompactNav = ref(false)

function updateNavMode() {
  isCompactNav.value = window.innerWidth < 1024
  if (!isCompactNav.value) {
    navOpen.value = true
  }
}

onMounted(() => {
  updateNavMode()
  window.addEventListener('resize', updateNavMode)
})

onUnmounted(() => {
  window.removeEventListener('resize', updateNavMode)
})

function handlePatientSelect(patient) {
  selectedPatient.value = patient
  searchExpanded.value = false
}

function handleClearPatient() {
  selectedPatient.value = null
  searchExpanded.value = true
}

function toggleNav() {
  navOpen.value = !navOpen.value
}

function closeNav() {
  if (isCompactNav.value) {
    navOpen.value = false
  }
}
</script>

<template>
  <div class="app-shell">
    <div class="bg-orbs" aria-hidden="true">
      <span class="orb orb-1" />
      <span class="orb orb-2" />
      <span class="orb orb-3" />
    </div>

    <header class="page-header">
      <div class="header-inner">
        <div class="header-left">
          <button
            v-if="isCompactNav"
            type="button"
            class="nav-toggle"
            :class="{ active: navOpen }"
            :aria-expanded="navOpen"
            :aria-label="navOpen ? 'Close project info' : 'Open project info'"
            @click="toggleNav"
          >
            <el-icon>
              <ArrowLeft v-if="navOpen" />
              <InfoFilled v-else />
            </el-icon>
            <span>{{ navOpen ? 'Close' : 'About' }}</span>
          </button>

          <div class="header-brand">
            <div class="brand-icon">+</div>
            <div>
              <div class="brand-eyebrow">HL7 · Fast Healthcare Interoperability Resources</div>
              <h1>HL7 FHIR Client</h1>
              <p class="header-tagline">Health IT - Patient search &amp; clinical document retrieval</p>
            </div>
          </div>
        </div>

        <div class="header-right">

          <div class="developer-card">
            <span class="developer-label">Developer</span>
            <p class="developer-name">John Pranoy Yalla</p>
            <div class="developer-links">
              <a
                href="https://github.com/johnpranoy7/"
                target="_blank"
                rel="noopener noreferrer"
                class="dev-link"
                title="GitHub"
              >
                <svg viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
                  <path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0024 12c0-6.63-5.37-12-12-12z"/>
                </svg>
                <span>GitHub</span>
              </a>
              <a
                href="https://www.linkedin.com/in/johnpranoy7/"
                target="_blank"
                rel="noopener noreferrer"
                class="dev-link"
                title="LinkedIn"
              >
                <svg viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
                  <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433a2.062 2.062 0 01-2.063-2.065 2.064 2.064 0 114.126 0 2.063 2.063 0 01-2.063 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
                </svg>
                <span>LinkedIn</span>
              </a>
            </div>
          </div>
        </div>
      </div>
    </header>

    <div class="app-body">
      <LeftNav
        :open="navOpen"
        :compact="isCompactNav"
        @close="closeNav"
      />

      <main class="page-layout" :class="{ 'has-selection': selectedPatient }">
        <PatientSearch
          :selected-patient="selectedPatient"
          :expanded="searchExpanded"
          @select="handlePatientSelect"
          @expand="searchExpanded = true"
          @collapse="searchExpanded = false"
          @clear="handleClearPatient"
        />
        <DocumentList :patient="selectedPatient" />
      </main>
    </div>
  </div>
</template>

<style scoped>
.app-shell {
  position: relative;
  min-height: 100vh;
  overflow-x: hidden;
  background: var(--app-bg);
}

.bg-orbs {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.55;
}

.orb-1 {
  width: min(520px, 90vw);
  height: min(520px, 90vw);
  top: -120px;
  left: -80px;
  background: radial-gradient(circle, var(--app-glow-cyan), transparent 70%);
}

.orb-2 {
  width: min(420px, 80vw);
  height: min(420px, 80vw);
  top: 20%;
  right: -100px;
  background: radial-gradient(circle, var(--app-glow-violet), transparent 70%);
}

.orb-3 {
  width: min(360px, 70vw);
  height: min(360px, 70vw);
  bottom: -80px;
  left: 35%;
  background: radial-gradient(circle, rgba(52, 211, 153, 0.12), transparent 70%);
}

.page-header {
  position: relative;
  z-index: 30;
  border-bottom: 1px solid var(--app-border);
  background: rgba(6, 8, 15, 0.82);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  padding: 18px 24px;
}

.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 0;
}

.nav-toggle {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 10px;
  border: 1px solid var(--app-border-strong);
  background: var(--app-accent-soft);
  color: var(--app-accent);
  font-size: 0.82rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.nav-toggle.active {
  background: rgba(34, 211, 238, 0.22);
  border-color: var(--app-accent);
}

.nav-toggle:hover {
  background: rgba(34, 211, 238, 0.2);
}

.header-brand {
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 0;
}

.brand-icon {
  flex-shrink: 0;
  width: 46px;
  height: 46px;
  border-radius: 14px;
  background: linear-gradient(135deg, #e20e0e 0%, #851616 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.5rem;
  font-weight: 700;
  line-height: 1;
  box-shadow: 0 12px 32px rgba(238, 34, 34, 0.3);
  padding-bottom: 0.8rem;
}

.brand-eyebrow {
  font-size: 0.72rem;
  text-transform: uppercase;
  letter-spacing: 0.14em;
  color: var(--app-accent);
  font-weight: 700;
  margin-bottom: 2px;
}

.page-header h1 {
  margin: 0;
  font-size: clamp(1.2rem, 2.5vw, 1.55rem);
  font-weight: 800;
  color: var(--app-text);
  letter-spacing: -0.02em;
}

.header-tagline {
  margin: 2px 0 0;
  color: var(--app-text-muted);
  font-size: clamp(0.78rem, 1.8vw, 0.92rem);
}

.header-right {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 14px;
}

.header-badge {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 14px;
  border-radius: 999px;
  background: var(--app-accent-soft);
  border: 1px solid var(--app-border-strong);
  color: var(--app-accent);
  font-size: 0.82rem;
  font-weight: 600;
  white-space: nowrap;
}

.developer-card {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 10px 14px;
  border-radius: 14px;
  background: rgba(15, 23, 42, 0.65);
  border: 1px solid var(--app-border);
  min-width: 0;
}

.developer-label {
  font-size: 0.68rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--app-text-dim);
}

.developer-name {
  margin: 0;
  font-size: 0.9rem;
  font-weight: 700;
  color: var(--app-text);
  white-space: nowrap;
}

.developer-links {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 2px;
}

.dev-link {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 0.78rem;
  font-weight: 600;
  color: var(--app-accent);
  text-decoration: none;
  transition: color 0.2s, opacity 0.2s;
}

.dev-link svg {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
}

.dev-link:hover {
  color: #67e8f9;
}

.pulse {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--app-accent);
  box-shadow: 0 0 12px var(--app-accent);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(0.85); }
}

.app-body {
  position: relative;
  z-index: 1;
  display: flex;
  width: 100%;
  min-height: calc(100vh - 73px);
}

.page-layout {
  flex: 1;
  min-width: 0;
  width: 100%;
  padding: clamp(16px, 2vw, 28px);
  display: flex;
  flex-direction: column;
  gap: clamp(16px, 2.5vw, 24px);
}

.page-layout.has-selection {
  gap: clamp(14px, 2vw, 20px);
}

@media (max-width: 1023px) {
  .brand-eyebrow {
    font-size: 0.65rem;
    letter-spacing: 0.08em;
  }
  

  .developer-card {
    padding: 8px 12px;
  }

  .developer-name {
    font-size: 0.82rem;
    display: flex;
    justify-content: center;
  }

  .developer-label{
    display: flex;
    justify-content: center;
  }
  .developer-links{
    display: flex;
    justify-content: center;
  }
}

@media (max-width: 767px) {
  .page-header {
    padding: 14px 16px;
  }

  .header-inner {
    flex-wrap: wrap;
    align-items: flex-start;
  }

  .header-right {
    width: 100%;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 10px;
  }

  .header-tagline {
    display: none;
  }

  .header-badge {
    font-size: 0.75rem;
    padding: 6px 10px;
  }

  .developer-card {
    flex: 1;
    min-width: 180px;
  }
}

@media (max-width: 480px) {
  .brand-eyebrow {
    display: none;
  }

  .header-badge span:not(.pulse) {
    display: none;
  }

  .header-badge {
    padding: 8px;
    border-radius: 50%;
    width: 36px;
    height: 36px;
    justify-content: center;
  }

  .dev-link span {
    display: none;
  }

  .developer-links {
    gap: 12px;
  }

  .dev-link svg {
    width: 18px;
    height: 18px;
  }
}
</style>
