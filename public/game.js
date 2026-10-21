// Themes matching Java enum definitions
const THEMES = {
  classic: {
    bg: '#0f1419',
    grid: '#18202a',
    head: '#2ed573',
    body: '#26af5f',
    food: '#ff4757',
    accent: '#ffd700',
    border: 'rgba(255, 71, 87, 0.35)'
  },
  cyberpunk: {
    bg: '#120b26',
    grid: '#201440',
    head: '#ff0080',
    body: '#9b2deb',
    food: '#00f5ff',
    accent: '#fff000',
    border: 'rgba(0, 245, 255, 0.4)'
  },
  retro: {
    bg: '#0c1c10',
    grid: '#142c19',
    head: '#8cdc32',
    body: '#5aa023',
    food: '#f0b428',
    accent: '#ffd700',
    border: 'rgba(240, 180, 40, 0.4)'
  },
  ocean: {
    bg: '#0a192f',
    grid: '#172a45',
    head: '#64ffda',
    body: '#46b4b4',
    food: '#ff6eb4',
    accent: '#ffc8dc',
    border: 'rgba(255, 110, 180, 0.4)'
  },
  light: {
    bg: '#f5f7fa',
    grid: '#dde3eb',
    head: '#16a05d',
    body: '#2ecc71',
    food: '#e74c3c',
    accent: '#d35400',
    border: 'rgba(231, 76, 60, 0.4)'
  }
};

const SPEEDS = {
  very_slow: 160,
  slow: 130,
  medium: 100,
  fast: 70,
  insane: 45
};

// Canvas & Grid Constants
const TILE_SIZE = 25;
const GRID_COLS = 26; // 650px
const GRID_ROWS = 26; // 650px

const canvas = document.getElementById('game-canvas');
const ctx = canvas.getContext('2d');

// UI Elements
const scoreEl = document.getElementById('score');
const highScoreEl = document.getElementById('high-score');
const pauseBtn = document.getElementById('pause-btn');
const restartBtn = document.getElementById('restart-btn');
const themeSelect = document.getElementById('theme-select');
const speedSelect = document.getElementById('speed-select');

// Overlay Elements
const overlay = document.getElementById('overlay');
const overlayTitle = document.getElementById('overlay-title');
const overlayScoreMsg = document.getElementById('overlay-score-msg');
const overlaySubMsg = document.getElementById('overlay-sub-msg');
const overlayBtn = document.getElementById('overlay-btn');

// Game State
let snake = [];
let food = { x: 0, y: 0 };
let direction = 'R';
let nextDirection = 'R';
let score = 0;
let highScore = parseInt(localStorage.getItem('snake_highscore') || '0', 10);
let running = false;
let paused = false;
let gameOver = false;
let gameInterval = null;
let currentTheme = 'classic';
let currentSpeed = 'medium';

highScoreEl.textContent = highScore;

// Initialize
function initGame() {
  snake = [];
  const startX = Math.floor(GRID_COLS / 4);
  const startY = Math.floor(GRID_ROWS / 2);
  snake.push({ x: startX, y: startY });
  snake.push({ x: startX - 1, y: startY });
  snake.push({ x: startX - 2, y: startY });
  snake.push({ x: startX - 3, y: startY });

  direction = 'R';
  nextDirection = 'R';
  score = 0;
  scoreEl.textContent = '0';
  gameOver = false;
  paused = false;
  running = true;

  pauseBtn.textContent = 'Pause';
  pauseBtn.style.backgroundColor = '#e1e8f0';

  hideOverlay();
  spawnFood();
  resetInterval();
  draw();
}

function resetInterval() {
  if (gameInterval) clearInterval(gameInterval);
  gameInterval = setInterval(gameStep, SPEEDS[currentSpeed]);
}

function spawnFood() {
  let valid = false;
  while (!valid) {
    const fx = Math.floor(Math.random() * GRID_COLS);
    const fy = Math.floor(Math.random() * GRID_ROWS);
    const hit = snake.some(seg => seg.x === fx && seg.y === fy);
    if (!hit) {
      food = { x: fx, y: fy };
      valid = true;
    }
  }
}

function gameStep() {
  if (paused || gameOver || !running) return;

  direction = nextDirection;
  const head = snake[0];
  const newHead = { x: head.x, y: head.y };

  if (direction === 'U') newHead.y--;
  if (direction === 'D') newHead.y++;
  if (direction === 'L') newHead.x--;
  if (direction === 'R') newHead.x++;

  // Wall collision
  if (newHead.x < 0 || newHead.x >= GRID_COLS || newHead.y < 0 || newHead.y >= GRID_ROWS) {
    endGame('Hit the wall!');
    return;
  }

  // Self-collision
  for (let i = 0; i < snake.length - 1; i++) {
    if (snake[i].x === newHead.x && snake[i].y === newHead.y) {
      endGame('Ran into self!');
      return;
    }
  }

  snake.unshift(newHead);

  // Fruit consumption
  if (newHead.x === food.x && newHead.y === food.y) {
    score += 10;
    scoreEl.textContent = score;

    if (score > highScore) {
      highScore = score;
      localStorage.setItem('snake_highscore', highScore);
      highScoreEl.textContent = highScore;
    }

    spawnFood();
  } else {
    snake.pop();
  }

  draw();
}

function endGame(reason) {
  gameOver = true;
  running = false;
  if (gameInterval) clearInterval(gameInterval);

  overlayTitle.textContent = 'GAME OVER';
  overlayScoreMsg.textContent = `Final Score: ${score}`;
  if (score >= highScore && score > 0) {
    overlaySubMsg.textContent = '★ NEW HIGH SCORE! ★';
  } else {
    overlaySubMsg.textContent = `Best Score: ${highScore}`;
  }
  overlayBtn.textContent = 'Play Again [R]';
  showOverlay();
  draw();
}

function togglePause() {
  if (gameOver || !running) return;

  paused = !paused;
  if (paused) {
    pauseBtn.textContent = 'Resume';
    pauseBtn.style.backgroundColor = '#b9f5cd';
    overlayTitle.textContent = 'PAUSED';
    overlayScoreMsg.textContent = `Current Score: ${score}`;
    overlaySubMsg.textContent = "Press 'P', 'Space', or Resume to continue";
    overlayBtn.textContent = 'Resume Game';
    showOverlay();
  } else {
    pauseBtn.textContent = 'Pause';
    pauseBtn.style.backgroundColor = '#e1e8f0';
    hideOverlay();
  }
}

function showOverlay() {
  overlay.classList.remove('hidden');
}

function hideOverlay() {
  overlay.classList.add('hidden');
}

// Drawing routines
function draw() {
  const t = THEMES[currentTheme];

  // Clear background
  ctx.fillStyle = t.bg;
  ctx.fillRect(0, 0, canvas.width, canvas.height);

  // Grid
  ctx.strokeStyle = t.grid;
  ctx.lineWidth = 1;
  for (let x = 0; x <= canvas.width; x += TILE_SIZE) {
    ctx.beginPath();
    ctx.moveTo(x, 0);
    ctx.lineTo(x, canvas.height);
    ctx.stroke();
  }
  for (let y = 0; y <= canvas.height; y += TILE_SIZE) {
    ctx.beginPath();
    ctx.moveTo(0, y);
    ctx.lineTo(canvas.width, y);
    ctx.stroke();
  }

  // Border
  ctx.strokeStyle = t.border;
  ctx.lineWidth = 3;
  ctx.strokeRect(1, 1, canvas.width - 2, canvas.height - 2);

  // Food
  const fx = food.x * TILE_SIZE;
  const fy = food.y * TILE_SIZE;

  // Glow
  ctx.fillStyle = t.border;
  ctx.beginPath();
  ctx.arc(fx + TILE_SIZE / 2, fy + TILE_SIZE / 2, TILE_SIZE / 2 + 2, 0, Math.PI * 2);
  ctx.fill();

  // Core
  ctx.fillStyle = t.food;
  ctx.beginPath();
  ctx.arc(fx + TILE_SIZE / 2, fy + TILE_SIZE / 2, TILE_SIZE / 2 - 2, 0, Math.PI * 2);
  ctx.fill();

  // Snake
  snake.forEach((seg, i) => {
    const sx = seg.x * TILE_SIZE;
    const sy = seg.y * TILE_SIZE;

    if (i === 0) {
      // Head
      ctx.fillStyle = t.head;
      roundRect(ctx, sx + 1, sy + 1, TILE_SIZE - 2, TILE_SIZE - 2, 6);
      drawEyes(sx, sy, direction);
    } else {
      // Body
      ctx.fillStyle = t.body;
      roundRect(ctx, sx + 2, sy + 2, TILE_SIZE - 4, TILE_SIZE - 4, 5);
    }
  });
}

function drawEyes(x, y, dir) {
  ctx.fillStyle = '#ffffff';
  let e1x = 0, e1y = 0, e2x = 0, e2y = 0;
  if (dir === 'U') {
    e1x = x + 4; e1y = y + 4;
    e2x = x + TILE_SIZE - 8; e2y = y + 4;
  } else if (dir === 'D') {
    e1x = x + 4; e1y = y + TILE_SIZE - 8;
    e2x = x + TILE_SIZE - 8; e2y = y + TILE_SIZE - 8;
  } else if (dir === 'L') {
    e1x = x + 4; e1y = y + 4;
    e2x = x + 4; e2y = y + TILE_SIZE - 8;
  } else if (dir === 'R') {
    e1x = x + TILE_SIZE - 8; e1y = y + 4;
    e2x = x + TILE_SIZE - 8; e2y = y + TILE_SIZE - 8;
  }

  ctx.beginPath();
  ctx.arc(e1x + 2, e1y + 2, 2.5, 0, Math.PI * 2);
  ctx.arc(e2x + 2, e2y + 2, 2.5, 0, Math.PI * 2);
  ctx.fill();

  ctx.fillStyle = '#000000';
  ctx.beginPath();
  ctx.arc(e1x + 2, e1y + 2, 1.2, 0, Math.PI * 2);
  ctx.arc(e2x + 2, e2y + 2, 1.2, 0, Math.PI * 2);
  ctx.fill();
}

function roundRect(ctx, x, y, w, h, r) {
  ctx.beginPath();
  ctx.moveTo(x + r, y);
  ctx.arcTo(x + w, y, x + w, y + h, r);
  ctx.arcTo(x + w, y + h, x, y + h, r);
  ctx.arcTo(x, y + h, x, y, r);
  ctx.arcTo(x, y, x + w, y, r);
  ctx.closePath();
  ctx.fill();
}

// Input Handlers
window.addEventListener('keydown', e => {
  const key = e.key;
  if (key === ' ' || key.toLowerCase() === 'p') {
    e.preventDefault();
    togglePause();
    return;
  }
  if (key.toLowerCase() === 'r') {
    initGame();
    return;
  }

  if (paused || gameOver) return;

  if ((key === 'ArrowUp' || key.toLowerCase() === 'w') && direction !== 'D') {
    nextDirection = 'U';
  } else if ((key === 'ArrowDown' || key.toLowerCase() === 's') && direction !== 'U') {
    nextDirection = 'D';
  } else if ((key === 'ArrowLeft' || key.toLowerCase() === 'a') && direction !== 'R') {
    nextDirection = 'L';
  } else if ((key === 'ArrowRight' || key.toLowerCase() === 'd') && direction !== 'L') {
    nextDirection = 'R';
  }
});

pauseBtn.addEventListener('click', togglePause);
restartBtn.addEventListener('click', initGame);
overlayBtn.addEventListener('click', () => {
  if (paused) {
    togglePause();
  } else {
    initGame();
  }
});

themeSelect.addEventListener('change', e => {
  currentTheme = e.target.value;
  document.body.setAttribute('data-theme', currentTheme);
  draw();
});

speedSelect.addEventListener('change', e => {
  currentSpeed = e.target.value;
  resetInterval();
});

// Start game on load
initGame();
