#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DEPLOY_ROOT="${DEPLOY_ROOT:-/opt/airline-booking}"
RELEASE_NAME="${RELEASE_NAME:-build-${BUILD_NUMBER:-$(date +%Y%m%d%H%M%S)}}"
JAVA_BIN="${JAVA_BIN:-java}"
JAVA_OPTS="${JAVA_OPTS:--Xms256m -Xmx512m}"
CURRENT_LINK="${DEPLOY_ROOT}/current"
RELEASES_DIR="${DEPLOY_ROOT}/releases"
LOG_DIR="${DEPLOY_ROOT}/logs"
RUN_DIR="${DEPLOY_ROOT}/run"
RELEASE_DIR="${RELEASES_DIR}/${RELEASE_NAME}"
PREVIOUS_RELEASE=""

SERVICES=(
  "config-server|cloud/config-server/target/config-server-0.0.1-SNAPSHOT.jar|8888"
  "service-registry|cloud/service-registry/target/service-registry-0.0.1-SNAPSHOT.jar|8761"
  "location-service|services/location-service/target/location-service-0.0.1-SNAPSHOT.jar|8001"
  "user-service|services/user-service/target/user-service-0.0.1-SNAPSHOT.jar|8002"
  "airline-core-service|services/airline-core-service/target/airline-core-service-0.0.1-SNAPSHOT.jar|8003"
  "pricing-service|services/pricing-service/target/pricing-service-0.0.1-SNAPSHOT.jar|8006"
  "seat-service|services/seat-service/target/seat-service-0.0.1-SNAPSHOT.jar|8007"
  "ancillary-service|services/ancillary-service/target/ancillary-service-0.0.1-SNAPSHOT.jar|8010"
  "fly-ops-service|services/fly-ops-service/target/fly-ops-service-0.0.1-SNAPSHOT.jar|8004"
  "payment-service|services/payment-service/target/payment-service-0.0.1-SNAPSHOT.jar|8009"
  "booking-service|services/booking-service/target/booking-service-0.0.1-SNAPSHOT.jar|8008"
  "api-gateway|cloud/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar|5000"
)

updated_services=()
mkdir -p "${RELEASES_DIR}" "${LOG_DIR}" "${RUN_DIR}" "${RELEASE_DIR}"
if [[ -L "${CURRENT_LINK}" ]]; then
  PREVIOUS_RELEASE="$(readlink -f "${CURRENT_LINK}")"
fi
log() {
  printf '[deploy] %s\n' "$*"
}
stop_service() {
  local service_name="$1"
  local pid_file="${RUN_DIR}/${service_name}.pid"
  if [[ -f "${pid_file}" ]]; then
    local pid
    pid="$(cat "${pid_file}")"
    if kill -0 "${pid}" >/dev/null 2>&1; then
      log "Stopping ${service_name} (pid ${pid})"
      kill "${pid}" >/dev/null 2>&1 || true
      for _ in {1..30}; do
        if ! kill -0 "${pid}" >/dev/null 2>&1; then
          break
        fi
        sleep 1
      done
      if kill -0 "${pid}" >/dev/null 2>&1; then
        kill -9 "${pid}" >/dev/null 2>&1 || true
      fi
    fi
    rm -f "${pid_file}"
  fi
}
wait_for_port() {
  local port="$1"
  local timeout_seconds="${2:-120}"
  local elapsed=0
  while (( elapsed < timeout_seconds )); do
    if (echo > "/dev/tcp/127.0.0.1/${port}") >/dev/null 2>&1; then
      return 0
    fi
    sleep 2
    elapsed=$((elapsed + 2))
  done
  return 1
}
start_service_from_release() {
  local source_root="$1"
  local release_dir="$2"
  local service_name="$3"
  local jar_rel_path="$4"
  local port="$5"
  local source_jar="${source_root}/${jar_rel_path}"
  local target_dir="${release_dir}/${service_name}"
  local target_jar=""
  local log_file="${LOG_DIR}/${service_name}.log"
  local pid_file="${RUN_DIR}/${service_name}.pid"
  if [[ ! -f "${source_jar}" ]]; then
    log "Missing artifact for ${service_name}: ${source_jar}"
    return 1
  fi
  mkdir -p "${target_dir}"
  cp "${source_jar}" "${target_dir}/"
  target_jar="${target_dir}/$(basename "${source_jar}")"
  log "Starting ${service_name} on port ${port}"
  nohup ${JAVA_BIN} ${JAVA_OPTS} -jar "${target_jar}" >"${log_file}" 2>&1 &
  echo $! > "${pid_file}"
  if ! wait_for_port "${port}" 180; then
    log "${service_name} failed to open port ${port}"
    tail -n 50 "${log_file}" || true
    return 1
  fi
  sleep 3
  if ! kill -0 "$(cat "${pid_file}")" >/dev/null 2>&1; then
    log "${service_name} exited immediately after startup"
    tail -n 50 "${log_file}" || true
    return 1
  fi
  return 0
}
rollback() {
  trap - ERR
  set +e
  log "Rolling back deployment"
  for service_entry in "${updated_services[@]}"; do
    IFS='|' read -r service_name _ _ <<< "${service_entry}"
    stop_service "${service_name}"
  done
  if [[ -n "${PREVIOUS_RELEASE}" && -d "${PREVIOUS_RELEASE}" ]]; then
    for service_entry in "${SERVICES[@]}"; do
      IFS='|' read -r service_name jar_rel_path port <<< "${service_entry}"
      if printf '%s\n' "${updated_services[@]}" | grep -Fxq "${service_name}|${jar_rel_path}|${port}"; then
        if ! start_service_from_release "${PREVIOUS_RELEASE}" "${PREVIOUS_RELEASE}" "${service_name}" "${jar_rel_path}" "${port}"; then
          log "Failed to restore ${service_name} from ${PREVIOUS_RELEASE}"
        fi
      fi
    done
    ln -sfn "${PREVIOUS_RELEASE}" "${CURRENT_LINK}"
  fi
  exit 1
}
trap 'rollback' ERR
log "Deploy root: ${DEPLOY_ROOT}"
log "Release name: ${RELEASE_NAME}"
log "Previous release: ${PREVIOUS_RELEASE:-none}"
for service_entry in "${SERVICES[@]}"; do
  IFS='|' read -r service_name jar_rel_path port <<< "${service_entry}"
  stop_service "${service_name}"
  updated_services+=("${service_name}|${jar_rel_path}|${port}")
  start_service_from_release "${ROOT_DIR}" "${RELEASE_DIR}" "${service_name}" "${jar_rel_path}" "${port}"
done
ln -sfn "${RELEASE_DIR}" "${CURRENT_LINK}"
log "Deployment finished successfully"
