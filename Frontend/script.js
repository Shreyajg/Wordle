let token = "";

async function login() {

    const username = document.getElementById("login-username").value;
    const password = document.getElementById("login-password").value;

    const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    });

    if (!response.ok) {
        const message = await response.text();
        document.getElementById("login-message").textContent = message;
        return;
    }

    const data = await response.json();

    token = data.token;

    document.getElementById("login-section").classList.add("hidden");
    document.getElementById("game-section").classList.remove("hidden");

    startGame();
}


async function startGame() {

    const response = await fetch("http://localhost:8080/games/start", {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token
        }
    });

    if (!response.ok) {
        const message = await response.text();
        document.getElementById("game-message").textContent = message;
        return;
    }

    const game = await response.json();

    document.getElementById("game-message").textContent =
        "Game started! You have 5 guesses.";
}


async function submitGuess() {

    const guessInput = document.getElementById("guess");
    const guess = guessInput.value;

    const response = await fetch("http://localhost:8080/games/guess", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        },
        body: JSON.stringify({
            guess: guess
        })
    });

    if (!response.ok) {
        const message = await response.text();
        document.getElementById("game-message").textContent = message;
        return;
    }

    const data = await response.json();

    displayGuess(guess.toUpperCase(), data.results);

    guessInput.value = "";

    if (data.status === "WON") {

        document.getElementById("game-message").textContent =
            "🎉 Congratulations! You guessed the word!";

        guessInput.disabled = true;
        document.querySelector("#guess-input button").disabled = true;

    } else if (data.status === "LOST") {

        document.getElementById("game-message").textContent =
            "Better luck next time!";

        guessInput.disabled = true;
        document.querySelector("#guess-input button").disabled = true;

    } else {

        document.getElementById("game-message").textContent =
            "Keep going!";
    }
}


function displayGuess(guess, results) {

    const guessesDiv = document.getElementById("guesses");

    const row = document.createElement("div");
    row.classList.add("guess-row");

    for (let i = 0; i < 5; i++) {

        const letter = document.createElement("div");

        letter.classList.add("letter");
        letter.classList.add(results[i].toLowerCase());

        letter.textContent = guess[i];

        row.appendChild(letter);
    }

    guessesDiv.appendChild(row);
}

async function register() {

    const username = document.getElementById("register-username").value;
    const password = document.getElementById("register-password").value;
    const confirmPassword = document.getElementById("confirm-password").value;

    const response = await fetch("http://localhost:8080/auth/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: username,
            password: password,
            confirmPassword: confirmPassword
        })
    });

    if (!response.ok) {
        const message = await response.text();
        document.getElementById("register-message").textContent = message;
        return;
    }

    const data = await response.json();

    token = data.token;

    document.getElementById("register-section").classList.add("hidden");
    document.getElementById("game-section").classList.remove("hidden");

    startGame();
}
function showRegister() {
    document.getElementById("login-section").classList.add("hidden");
    document.getElementById("register-section").classList.remove("hidden");
}