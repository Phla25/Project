
        let allMovies = [];
        let allActors = [];

        // DOM elements for Movie Modal
        const searchInput = document.getElementById('searchInput');
        const genreSelect = document.getElementById('genreSelect');
        const moviesList = document.getElementById('moviesList'); 
        const loadingContainer = document.getElementById('loadingContainer');
        const resultsCount = document.getElementById('resultsCount');
        const movieModal = document.getElementById('movieModal');
        const modalTitle = document.getElementById('modalTitle');
        const openBtn = document.getElementById('openModalBtn');
        const closeBtn = document.querySelector('#movieModal .close');
        const cancelBtn = document.getElementById('cancelBtn');
        const movieForm = document.getElementById('movieForm');
        const alertContainer = document.getElementById('alertContainer');
        const formLoading = document.querySelector('#movieModal .form-loading');
        const formLoadingText = document.getElementById('formLoadingText');
        const submitBtn = document.getElementById('submitBtn');
        const movieIDInput = document.getElementById('movieID');
        const priceInput = document.getElementById('price');

        // DOM elements for Actor Modal
        const actorModal = document.getElementById('actorModal');
        const actorModalTitle = document.getElementById('actorModalTitle');
        const actorCloseBtn = document.querySelector('#actorModal .close');
        const actorCancelBtn = document.getElementById('actorCancelBtn');
        const actorForm = document.getElementById('actorForm');
        const actorAlertContainer = document.getElementById('actorAlertContainer');
        const actorFormLoading = document.querySelector('#actorModal .form-loading');
        const actorFormLoadingText = document.getElementById('actorFormLoadingText');
        const actorSubmitBtn = document.getElementById('actorSubmitBtn');
        // const actorConfirmBtn = document.getElementById('actorConfirmBtn'); // No longer needed
        const actorMovieIDInput = document.getElementById('actorMovieID');
        const actorsGrid = document.getElementById('actorsGrid');
        const actorSearch = document.getElementById('actorSearch');

        let currentMovies = allMovies;
        let isUpdateMode = false;

        // Fallback image for actors and movies (simplified for minimalist design)
        const FALLBACK_MOVIE_IMAGE = 'https://via.placeholder.com/100x150?text=No+Poster';
        const FALLBACK_ACTOR_IMAGE = 'https://via.placeholder.com/90x90?text=No+Photo'; // Not used in display now but good to keep as fallback


        // Initialize page
        document.addEventListener('DOMContentLoaded', function() {
            loadMoviesFromBackend();
            setDefaultDate();
        });

        // Load movies from backend
        async function loadMoviesFromBackend(genre = '', title = '') {
            showLoading(true);
            
            try {
                let url = '/movies'; // Assuming this endpoint provides all movie data
                if (genre || title) {
                    const params = new URLSearchParams();
                    if (genre) params.append('genre', genre);
                    if (title) params.append('title', title);
                    url += `?${params.toString()}`;
                }
                const response = await fetch(url, {
                    headers: {
                        'Accept': 'application/json'
                    }
                });
                
                if (response.ok) {
                    allMovies = await response.json();
                    currentMovies = allMovies;
                    applyFilters(); // Apply filters initially after loading all movies
                } else {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
            } catch (error) {
                console.error('Error loading movies:', error);
                showError(`Failed to load movies: ${error.message}. Please check your connection and try again.`);
            } finally {
                showLoading(false);
            }
        }

        // Load actors from backend
        async function loadActorsFromBackend(movieID) {
            actorFormLoading.style.display = 'block';
            actorFormLoadingText.textContent = 'Đang tải diễn viên...';
            actorsGrid.innerHTML = ''; // Clear previous actors

            try {
                const actorResponse = await fetch('/actor', {
                    headers: {
                        'Accept': 'application/json'
                    }
                });

                if (!actorResponse.ok) {
                    throw new Error(`Failed to fetch actors: ${actorResponse.status} ${actorResponse.statusText}`);
                }

                allActors = await actorResponse.json();
                if (!Array.isArray(allActors)) {
                    throw new Error('Invalid response format: Expected an array of actors');
                }

                // Filter out actors already associated with the current movie
                const movieToFind = currentMovies.find(m => m.movieID == movieID);
                let associatedActorIDs = [];
                if (movieToFind && movieToFind.actors) {
                    associatedActorIDs = movieToFind.actors.map(actor => actor.actorID);
                }

                allActors = allActors.filter(actor => !associatedActorIDs.includes(actor.actorID));

                displayActors(); // Display filtered actors
            } catch (error) {
                console.error('Error loading actors:', error);
                actorsGrid.innerHTML = `
                    <div class="error-container">
                        <h3>⚠️ Lỗi Tải Diễn Viên</h3>
                        <p>Không thể tải diễn viên: ${error.message}</p>
                        <button onclick="loadActorsFromBackend(${movieID})" class="btn btn-primary" style="margin-top: 15px;">
                            Thử lại
                        </button>
                    </div>
                `;
            } finally {
                actorFormLoading.style.display = 'none';
            }
        }

        // Display actors in grid (simplified)
        function displayActors(searchTerm = '') {
            actorsGrid.innerHTML = '';
            const filteredActors = searchTerm 
                ? allActors.filter(actor => actor.name.toLowerCase().includes(searchTerm.toLowerCase()))
                : allActors;

            if (!filteredActors || filteredActors.length === 0) {
                actorsGrid.innerHTML = `
                    <div class="no-actors">
                        <h3>Không tìm thấy diễn viên</h3>
                        <p>${searchTerm ? 'Không có diễn viên nào phù hợp với tìm kiếm của bạn.' : 'Không có diễn viên nào trong cơ sở dữ liệu hoặc đã được liên kết với phim này.'}</p>
                    </div>
                `;
                return;
            }

            actorsGrid.innerHTML = filteredActors.map(actor => `
                <div class="actor-card">
                    <input type="checkbox" name="actorIDs" id="actor_${actor.actorID}" value="${actor.actorID || ''}" 
                            aria-label="Chọn ${actor.name}">
                    <div class="actor-name">${actor.name || 'Diễn viên ẩn danh'}</div>
                </div>
            `).join('');
        }

        // Actor search functionality
        actorSearch.addEventListener('input', () => {
            const searchTerm = actorSearch.value.trim();
            displayActors(searchTerm);
        });

        // Search and genre filter functionality
        searchInput.addEventListener('input', applyFilters);
        genreSelect.addEventListener('change', function() {
            const selectedGenre = this.value;
            loadMoviesFromBackend(selectedGenre, searchInput.value); // Re-fetch based on filter
        });

        // Apply filters (genre and search)
        function applyFilters() {
            const searchText = searchInput.value.toLowerCase();
            const selectedGenre = genreSelect.value;

            currentMovies = allMovies.filter(movie => {
                const matchesGenre = selectedGenre ? movie.genre === selectedGenre : true;
                const matchesSearch = movie.title.toLowerCase().includes(searchText);
                return matchesGenre && matchesSearch;
            });

            loadMovies();
            updateResultsCount(selectedGenre, searchText);
        }

        // Helper function to format price to USD with comma as thousands separator and point as decimal separator
        function formatPriceToUSD(price) {
            if (price == null) {
                return '0.00$';
            }
            const numPrice = parseFloat(price);
            if (isNaN(numPrice)) {
                return '0.00$'; 
            }
            const fixedPrice = numPrice.toFixed(2);
            const parts = fixedPrice.split('.');
            parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',');
            return parts.join('.') + '$';
        }


        // Load and display movies (changed to list view)
        function loadMovies() {
            if (!currentMovies || currentMovies.length === 0) {
                moviesList.innerHTML = `
                    <div class="no-movies">
                        <h3>Không tìm thấy phim</h3>
                        <p>Hãy thử điều chỉnh tìm kiếm hoặc lọc thể loại, hoặc thêm một vài bộ phim!</p>
                    </div>
                `;
                return;
            }

            moviesList.innerHTML = currentMovies.map(movie => `
                <div class="movie-item">
                    <img src="${movie.posterImageURL || FALLBACK_MOVIE_IMAGE}" 
                            alt="${movie.title || 'Không có tiêu đề'}" 
                            class="movie-poster"
                            onerror="this.src='${FALLBACK_MOVIE_IMAGE}'">
                    
                    <div class="movie-info">
                        <h3 class="movie-title">${movie.title || 'Không có tiêu đề'}</h3>
                        <div class="movie-meta">
                            <span>ID: ${movie.movieID || 'N/A'}</span>
                            ${movie.genre ? `<span class="movie-genre">Thể loại: ${movie.genre}</span>` : ''}
                            ${movie.releaseDate ? `<span>Ngày phát hành: ${formatDate(movie.releaseDate)}</span>` : ''}
                            ${movie.price != null ? `<span class="movie-price">${formatPriceToUSD(movie.price)}</span>` : '<span class="movie-price">Giá chưa đặt</span>'}
                            ${movie.directorName ? `<span>Đạo diễn: ${movie.directorName}</span>` : ''}
                            ${movie.studioName ? `<span>Studio: ${movie.studioName}</span>` : ''}
                            <div class="movie-actors">
                                <strong>Diễn viên:</strong> 
                                ${movie.actors && movie.actors.length > 0 
                                    ? movie.actors.map(actor => actor.name || 'Ẩn danh').join(', ')
                                    : 'Chưa có'}
                            </div>
                        </div>
                        <div class="movie-actions">
                            <button class="update-btn" onclick="openUpdateModal(${movie.movieID})" aria-label="Cập nhật ${movie.title}">Cập nhật</button>
                            <button class="add-actor-btn" onclick="openAddActorModal(${movie.movieID})" aria-label="Thêm diễn viên vào ${movie.title}">Thêm Diễn Viên</button>
                        </div>
                    </div>
                </div>
            `).join('');
        }

        // Open update modal with pre-filled data
        function openUpdateModal(movieID) {
            const movie = currentMovies.find(m => m.movieID === movieID);
            if (!movie) {
                showAlert('Không tìm thấy phim.', 'error');
                return;
            }

            isUpdateMode = true;
            modalTitle.textContent = 'Cập Nhật Phim';
            submitBtn.textContent = 'Cập Nhật Phim';
            submitBtn.setAttribute('aria-label', 'Cập Nhật Phim');
            formLoadingText.textContent = 'Đang cập nhật phim...';

            document.getElementById('movieID').value = movie.movieID || '';
            document.getElementById('title').value = movie.title || '';
            document.getElementById('posterImageURL').value = movie.posterImageURL || '';
            document.getElementById('releaseDate').value = movie.releaseDate ? new Date(movie.releaseDate).toISOString().split('T')[0] : '';
            document.getElementById('price').value = movie.price != null ? parseFloat(movie.price).toFixed(2) : '';
            document.getElementById('genre').value = movie.genre || '';
            document.getElementById('studioID').value = movie.studioID || '';
            document.getElementById('directorID').value = movie.directorID || '';
            // document.getElementById('discountID').value = movie.discountID || ''; // Removed

            movieModal.style.display = 'flex'; // Use flex to center modal
            document.body.style.overflow = 'hidden';
        }

        // Open add actor modal
        function openAddActorModal(movieID) {
            const movie = currentMovies.find(m => m.movieID === movieID);
            if (!movie) {
                showActorAlert('Không tìm thấy phim.', 'error');
                return;
            }

            actorModalTitle.textContent = `Thêm Diễn Viên vào "${movie.title}"`;
            actorMovieIDInput.value = movieID;
            actorSearch.value = ''; // Clear search input when opening modal
            loadActorsFromBackend(movieID);
            actorModal.style.display = 'flex'; // Use flex to center modal
            document.body.style.overflow = 'hidden';
        }

        // Show error message for movies
        function showError(message) {
            moviesList.innerHTML = `
                <div class="error-container">
                    <h3>⚠️ Lỗi Tải Phim</h3>
                    <p>${message}</p>
                    <button onclick="loadMoviesFromBackend()" class="btn btn-primary" style="margin-top: 15px;">
                        Thử lại
                    </button>
                </div>
            `;
        }

        // Update results count
        function updateResultsCount(genre, searchText) {
            const count = currentMovies.length;
            const genreText = genre ? ` trong "${genre}"` : '';
            const searchTextDisplay = searchText ? ` khớp với "${searchText}"` : '';
            resultsCount.textContent = `Tìm thấy ${count} phim${count !== 1 ? '' : ''}${genreText}${searchTextDisplay}`;
            resultsCount.style.display = 'block';
        }

        // Show/hide loading
        function showLoading(show) {
            loadingContainer.style.display = show ? 'block' : 'none';
            moviesList.style.display = show ? 'none' : 'flex'; // Ensure moviesList is visible when not loading
        }

        // Format date
        function formatDate(dateString) {
            const date = new Date(dateString);
            if (isNaN(date)) return 'Ngày không hợp lệ';
            return date.toLocaleDateString('vi-VN', {
                year: 'numeric', 
                month: 'short', 
                day: 'numeric' 
            });
        }

        // Movie Modal functionality
        openBtn.addEventListener('click', () => {
            isUpdateMode = false;
            modalTitle.textContent = 'Thêm Phim Mới';
            submitBtn.textContent = 'Thêm Phim';
            submitBtn.setAttribute('aria-label', 'Thêm Phim');
            formLoadingText.textContent = 'Đang thêm phim...';
            movieForm.reset();
            setDefaultDate();
            priceInput.value = ''; // Ensure price is cleared for new entries
            movieModal.style.display = 'flex'; // Use flex to center modal
            document.body.style.overflow = 'hidden';
            alertContainer.innerHTML = ''; // Clear any previous alerts
            formLoading.style.display = 'none'; // Hide form loading initially
            submitBtn.disabled = false; // Enable submit button
        });

        function closeMovieModal() {
            movieModal.style.display = 'none';
            document.body.style.overflow = 'auto';
            movieForm.reset();
            alertContainer.innerHTML = '';
            formLoading.style.display = 'none';
            submitBtn.disabled = false;
            isUpdateMode = false;
            setDefaultDate();
        }

        closeBtn.addEventListener('click', closeMovieModal);
        cancelBtn.addEventListener('click', closeMovieModal);

        // Actor Modal functionality
        function closeActorModal() {
            actorModal.style.display = 'none';
            document.body.style.overflow = 'auto';
            actorForm.reset();
            actorAlertContainer.innerHTML = '';
            actorFormLoading.style.display = 'none';
            actorSubmitBtn.disabled = false;
            actorSearch.value = ''; // Clear search input
            actorsGrid.innerHTML = ''; // Clear actors grid
        }

        actorCloseBtn.addEventListener('click', closeActorModal);
        actorCancelBtn.addEventListener('click', closeActorModal);

        window.addEventListener('click', (e) => {
            if (e.target === movieModal) {
                closeMovieModal();
            }
            if (e.target === actorModal) {
                closeActorModal();
            }
        });

        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                // Check which modal is open and close it
                if (movieModal.style.display === 'flex') { // Check for 'flex' as it's the current display style
                    closeMovieModal();
                } else if (actorModal.style.display === 'flex') { // Check for 'flex'
                    closeActorModal();
                }
            }
        });

        // Movie Form submission
        movieForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            // Client-side validation
            const title = document.getElementById('title').value.trim();
            const releaseDate = document.getElementById('releaseDate').value;
            if (!title) {
                showAlert('Tên phim là bắt buộc.', 'error');
                return;
            }
            if (!releaseDate) {
                showAlert('Ngày phát hành là bắt buộc.', 'error');
                return;
            }

            formLoading.style.display = 'block';
            submitBtn.disabled = true;
            alertContainer.innerHTML = '';

            const formData = new FormData(movieForm);
            const movieID = formData.get('movieID');
            const url = isUpdateMode ? `/manager/movies/${movieID}` : '/manager/movies';
            const method = isUpdateMode ? 'PUT' : 'POST';

            try {
                const response = await fetch(url, {
                    method: method,
                    body: formData,
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });

                const result = await response.json();
                
                formLoading.style.display = 'none';
                submitBtn.disabled = false;

                if (result.success) {
                    showAlert(`${result.message} (Giá: ${formatPriceToUSD(parseFloat(priceInput.value))})`, 'success');
                    setTimeout(() => {
                        closeMovieModal();
                        loadMoviesFromBackend(genreSelect.value, searchInput.value);
                    }, 1500);
                } else {
                    showAlert(result.message, 'error');
                }
            } catch (error) {
                formLoading.style.display = 'none';
                submitBtn.disabled = false;
                showAlert(`Có lỗi xảy ra khi ${isUpdateMode ? 'cập nhật' : 'thêm'} phim: ${error.message}.`, 'error');
                console.error('Error:', error);
            }
        });

        // Actor Form submission (simplified, no explicit confirm button anymore)
        actorForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const selectedActors = Array.from(document.querySelectorAll('input[name="actorIDs"]:checked'));
            if (selectedActors.length === 0) {
                showActorAlert('Vui lòng chọn ít nhất một diễn viên.', 'error');
                return;
            }

            actorFormLoading.style.display = 'block';
            actorFormLoadingText.textContent = 'Đang thêm diễn viên...';
            actorSubmitBtn.disabled = true;
            actorAlertContainer.innerHTML = '';

            const formData = new FormData();
            const movieID = actorMovieIDInput.value;
            
            // Collect actor IDs and assign a default role
            const actorsToAdd = selectedActors.map(checkbox => ({
                actorID: checkbox.value,
                role: 'Diễn viên phụ' // Default role since input is removed
            }));

            formData.append('movieID', movieID);
            actorsToAdd.forEach(actor => {
                formData.append('actorIDs', actor.actorID);
                formData.append('roles', actor.role);
            });

            try {
                const response = await fetch(`/manager/movies/${movieID}/actor`, {
                    method: 'POST',
                    body: formData,
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });

                const result = await response.json();
                
                actorFormLoading.style.display = 'none';
                actorSubmitBtn.disabled = false;

                if (result.success) {
                    showActorAlert(result.message, 'success');
                    setTimeout(() => {
                        closeActorModal();
                        loadMoviesFromBackend(genreSelect.value, searchInput.value);
                    }, 1500);
                } else {
                    const errorMessage = result.message.includes('Some actors could not be added')
                        ? result.message
                        : `Thêm diễn viên thất bại: ${result.message}`;
                    showActorAlert(errorMessage, 'error');
                }
            } catch (error) {
                actorFormLoading.style.display = 'none';
                actorSubmitBtn.disabled = false;
                showActorAlert(`Có lỗi xảy ra khi thêm diễn viên: ${error.message}.`, 'error');
                console.error('Error:', error);
            }
        });

        // Show alert function for Movie Modal
        function showAlert(message, type) {
            alertContainer.innerHTML = `
                <div class="alert alert-${type}">
                    ${message}
                </div>
            `;
            alertContainer.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }

        // Show alert function for Actor Modal
        function showActorAlert(message, type) {
            actorAlertContainer.innerHTML = `
                <div class="alert alert-${type}">
                    ${message}
                </div>
            `;
            actorAlertContainer.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }

        // Set default date to today
        function setDefaultDate() {
            const releaseDateInput = document.getElementById('releaseDate');
            if (releaseDateInput) {
                const today = new Date();
                releaseDateInput.value = today.toISOString().split('T')[0];
            }
        }