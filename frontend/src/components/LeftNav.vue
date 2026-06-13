<script setup>
import { ArrowLeft, Close, Link } from '@element-plus/icons-vue'

defineProps({
  open: {
    type: Boolean,
    default: true
  },
  compact: {
    type: Boolean,
    default: false
  }
})

defineEmits(['close'])

const fhirServer = 'http://hapi.fhir.org/baseR4/'

const endpoints = [
  {
    step: 1,
    label: 'Patient Search',
    path: '/Patient?name={firstName}&family={lastName}',
    description: 'Search the FHIR test server for patients by given name and/or family name.'
  },
  {
    step: 2,
    label: 'Document Lookup',
    path: '/DocumentReference?patient={patientId}',
    description: 'Retrieve all document references linked to the selected patient.'
  },
  {
    step: 3,
    label: 'Binary Download',
    path: '/Binary/{documentId}',
    description: 'Download the actual document file when a Binary resource is available.'
  }
]

const flowSteps = [
  'FHIR Test Server responds with JSON Bundles',
  'Spring Boot REST client parses FHIR resources',
  'Mappers convert resources to DTOs',
  'Vue UI displays data and handles downloads'
]
</script>

<template>
  <aside
    class="left-nav"
    :class="{ open, compact }"
    :aria-hidden="compact && !open"
  >
    <div v-if="compact" class="nav-drawer-header">
      <h2 class="nav-title">Project Info</h2>
      <button type="button" class="nav-close-btn" aria-label="Close panel" @click="$emit('close')">
        <el-icon><ArrowLeft /></el-icon>
        <span>Close</span>
      </button>
    </div>

    <button
      v-if="compact && open"
      type="button"
      class="drawer-edge-close"
      aria-label="Close panel"
      @click="$emit('close')"
    >
      <el-icon><Close /></el-icon>
    </button>

    <div class="nav-content">
      <div class="nav-section">
        <h2 v-if="!compact" class="nav-title">About This Project</h2>
        <p class="nav-text">
          This application connects to the public HAPI FHIR R4 test server to search
          for patients and download their available clinical documents.
        </p>
      </div>

      <div class="nav-section">
        <h3 class="nav-subtitle">FHIR Test Server</h3>
        <a :href="fhirServer" target="_blank" rel="noopener noreferrer" class="server-link">
          <el-icon><Link /></el-icon>
          <span>hapi.fhir.org/baseR4</span>
        </a>
      </div>

      <div class="nav-section">
        <h3 class="nav-subtitle">REST Endpoints Used</h3>
        <ul class="endpoint-list">
          <li v-for="endpoint in endpoints" :key="endpoint.step" class="endpoint-item">
            <span class="endpoint-step">{{ endpoint.step }}</span>
            <div class="endpoint-body">
              <strong>{{ endpoint.label }}</strong>
              <code>{{ endpoint.path }}</code>
              <p>{{ endpoint.description }}</p>
            </div>
          </li>
        </ul>
      </div>

      <div class="nav-section">
        <h3 class="nav-subtitle">Data Flow</h3>
        <ol class="flow-list">
          <li v-for="(step, index) in flowSteps" :key="index">
            <span class="flow-dot" />
            {{ step }}
          </li>
        </ol>
      </div>

      <div class="nav-footer">
        <span class="stack-badge">Spring Boot</span>
        <span class="stack-badge">HAPI FHIR</span>
        <span class="stack-badge">Vue 3</span>
      </div>
    </div>
  </aside>
</template>

<style scoped>
.left-nav {
  width: var(--nav-width, 380px);
  flex-shrink: 0;
  height: calc(100vh - 73px);
  position: sticky;
  top: 73px;
  overflow: hidden;
  border-right: 1px solid var(--app-border);
  background: rgba(6, 8, 15, 0.88);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  transition: transform 0.3s ease, box-shadow 0.3s ease, width 0.2s ease;
}

@media (min-width: 1024px) and (max-width: 1279px) {
  .left-nav:not(.compact) {
    width: 280px;
  }
}

@media (min-width: 1280px) and (max-width: 1599px) {
  .left-nav:not(.compact) {
    width: 320px;
  }
}

.nav-content {
  height: 100%;
  overflow-y: auto;
  padding: 28px 26px 32px;
}

.nav-drawer-header {
  display: none;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--app-border);
}

.nav-close-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 10px;
  border: 1px solid var(--app-border-strong);
  background: var(--app-accent-soft);
  color: var(--app-accent);
  font-size: 0.84rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.nav-close-btn:hover {
  background: rgba(34, 211, 238, 0.22);
}

.drawer-edge-close {
  display: none;
}

.nav-section {
  margin-bottom: 28px;
}

.nav-section:last-of-type {
  margin-bottom: 24px;
}

.nav-title {
  margin: 0 0 12px;
  font-size: 1.1rem;
  font-weight: 800;
  color: var(--app-text);
  letter-spacing: -0.01em;
}

.nav-subtitle {
  margin: 0 0 12px;
  font-size: 0.8rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--app-accent);
}

.nav-text {
  margin: 0;
  font-size: 0.92rem;
  line-height: 1.65;
  color: var(--app-text-muted);
}

.server-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 12px;
  background: var(--app-accent-soft);
  border: 1px solid var(--app-border-strong);
  color: var(--app-accent);
  font-size: 0.88rem;
  font-weight: 600;
  text-decoration: none;
  transition: background 0.2s, transform 0.2s;
  word-break: break-word;
}

.server-link:hover {
  background: rgba(34, 211, 238, 0.2);
  transform: translateY(-1px);
}

.endpoint-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.endpoint-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.endpoint-step {
  flex-shrink: 0;
  width: 28px;
  height: 28px;
  border-radius: 9px;
  background: linear-gradient(135deg, #0891b2, #6366f1);
  color: #fff;
  font-size: 0.8rem;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 2px;
}

.endpoint-body {
  flex: 1;
  min-width: 0;
}

.endpoint-body strong {
  display: block;
  font-size: 0.92rem;
  color: var(--app-text);
  margin-bottom: 6px;
}

.endpoint-body code {
  display: block;
  font-size: 0.78rem;
  font-family: 'Consolas', 'Monaco', monospace;
  color: var(--app-accent-2);
  background: rgba(129, 140, 248, 0.1);
  border: 1px solid rgba(129, 140, 248, 0.2);
  border-radius: 8px;
  padding: 8px 10px;
  margin-bottom: 8px;
  word-break: break-word;
  line-height: 1.5;
  white-space: pre-wrap;
}

.endpoint-body p {
  margin: 0;
  font-size: 0.84rem;
  line-height: 1.55;
  color: var(--app-text-dim);
}

.flow-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.flow-list li {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  font-size: 0.88rem;
  line-height: 1.55;
  color: var(--app-text-muted);
}

.flow-dot {
  flex-shrink: 0;
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: var(--app-accent);
  margin-top: 6px;
  box-shadow: 0 0 8px var(--app-accent);
}

.nav-footer {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid var(--app-border);
}

.stack-badge {
  font-size: 0.72rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  padding: 6px 10px;
  border-radius: 8px;
  background: rgba(30, 41, 59, 0.8);
  border: 1px solid var(--app-border);
  color: var(--app-text-dim);
}

/* Laptop — removed duplicate, handled above */

/* Tablet & mobile drawer — solid panel, no overlay */
@media (max-width: 1023px) {
  .left-nav.compact {
    position: fixed;
    overflow: visible;
    top: 0;
    left: 0;
    z-index: 40;
    width: min(400px, 90vw);
    height: 100vh;
    height: 100dvh;
    transform: translateX(-105%);
    box-shadow: none;
    background: #0a0e17;
    backdrop-filter: none;
    -webkit-backdrop-filter: none;
    border-right: 1px solid rgba(148, 163, 184, 0.12);
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  .left-nav.compact.open {
    transform: translateX(0);
    box-shadow: 8px 0 32px rgba(0, 0, 0, 0.35);
  }

  .nav-drawer-header {
    display: flex;
    background: #0a0e17;
    position: sticky;
    top: 0;
    z-index: 1;
  }

  .nav-drawer-header .nav-title {
    margin: 0;
  }

  .drawer-edge-close {
    display: flex;
    align-items: center;
    justify-content: center;
    position: absolute;
    top: 50%;
    right: -18px;
    transform: translateY(-50%);
    width: 36px;
    height: 56px;
    border-radius: 0 12px 12px 0;
    border: 1px solid var(--app-border-strong);
    border-left: none;
    background: #0a0e17;
    color: var(--app-accent);
    cursor: pointer;
    z-index: 2;
    box-shadow: 4px 0 16px rgba(0, 0, 0, 0.25);
  }

  .drawer-edge-close:hover {
    background: #141b2d;
  }

  .nav-content {
    height: calc(100vh - 65px);
    height: calc(100dvh - 65px);
    padding: 24px 22px 32px;
    background: #0a0e17;
  }

  .endpoint-body code {
    background: #141b2d;
    border-color: rgba(129, 140, 248, 0.25);
  }

  .stack-badge {
    background: #141b2d;
  }
}

@media (max-width: 767px) {
  .left-nav.compact {
    width: 100vw;
  }
}
</style>
