// main.js

let allMovies = [];
let currentMovies = allMovies; // Dùng để lưu trữ phim sau khi lọc

// DOM elements chính
const searchInput = document.getElementById('searchInput');
const genreSelect = document.getElementById('genreSelect');
const moviesList = document.getElementById('moviesList');
const loadingContainer = document.getElementById('loadingContainer');
const resultsCount = document.getElementById('resultsCount');

// Fallback image cho phim
const FALLBACK_MOVIE_IMAGE = 'https://placehold.co/100x150/cccccc/333333?text=No+Poster';

// Khởi tạo trang khi DOM đã tải xong
document.addEventListener('DOMContentLoaded', function() {
    loadMoviesFromBackend();
});

// Khởi tạo trang khi DOM đã tải xong
document.addEventListener('DOMContentLoaded', function() {
    // Lấy tham số genre từ URL
    const urlParams = new URLSearchParams(window.location.search);
    const genreFromUrl = urlParams.get('genre');
    
    // Nếu có tham số genre từ URL, đặt giá trị cho genreSelect và tải phim
    if (genreFromUrl) {
        // Đảm bảo option genre đó tồn tại trước khi chọn
        const optionExists = Array.from(genreSelect.options).some(option => option.value === genreFromUrl);
        if (optionExists) {
            genreSelect.value = genreFromUrl;
            loadMoviesFromBackend(genreFromUrl, searchInput.value);
        } else {
            // Nếu genre từ URL không hợp lệ, tải tất cả phim
            loadMoviesFromBackend();
        }
    } else {
        // Nếu không có tham số genre, tải tất cả phim như bình thường
        loadMoviesFromBackend();
    }
});

// Load movies từ backend
async function loadMoviesFromBackend(genre = '', title = '') {
    showLoading(true);

    try {
        let url = '/dashboard'; // Endpoint API để lấy dữ liệu phim
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
            currentMovies = allMovies; // Cập nhật danh sách phim hiện tại
            applyFilters(); // Áp dụng bộ lọc ban đầu sau khi tải tất cả phim
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

// Hàm helper để định dạng giá thành USD
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

// Hàm helper để định dạng ngày
function formatDate(dateString) {
    const date = new Date(dateString);
    if (isNaN(date)) return 'Ngày không hợp lệ';
    return date.toLocaleDateString('vi-VN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// Hàm helper để lấy các tên duy nhất từ chuỗi (cho đạo diễn, studio)
function getUniqueNamesFromString(nameString) {
    // Kiểm tra nếu nameString không phải là chuỗi hoặc rỗng/null/undefined
    if (typeof nameString !== 'string' || !nameString) {
        return 'N/A'; // Trả về 'N/A' nếu không hợp lệ
    }
    const names = nameString.split(',').map(name => name.trim());
    const uniqueNames = [...new Set(names)];
    return uniqueNames.join(', ');
}

// Áp dụng bộ lọc (thể loại và tìm kiếm)
function applyFilters() {
    const searchText = searchInput.value.toLowerCase();
    const selectedGenre = genreSelect.value;

    currentMovies = allMovies.filter(movie => {
        const matchesGenre = selectedGenre ? movie.genre === selectedGenre : true;
        const matchesSearch = movie.title.toLowerCase().includes(searchText);
        return matchesGenre && matchesSearch;
    });

    loadMovies(); // Tải lại phim sau khi lọc
    updateResultsCount(selectedGenre, searchText);
}

// Tải và hiển thị phim (chế độ xem thẻ)
function loadMovies() {
    if (!currentMovies || currentMovies.length === 0) {
        moviesList.innerHTML = `
            <div class="no-movies">
                <h3>Không tìm thấy phim</h3>
                <p>Hãy thử điều chỉnh tìm kiếm hoặc lọc thể loại.</p>
            </div>
        `;
        return;
    }

    moviesList.innerHTML = currentMovies.map(movie => `
        <div class="movie-card">
            <img src="${movie.posterImageURL || FALLBACK_MOVIE_IMAGE}"
                 alt="${movie.title || 'Không có tiêu đề'}"
                 class="movie-card-poster"
                 onerror="this.src='${FALLBACK_MOVIE_IMAGE}'">

            <div class="movie-card-content">
                <h3 class="movie-card-title">${movie.title || 'Không có tiêu đề'}</h3>
                <div class="movie-card-meta">
                    <p><strong>ID:</strong> ${movie.movieID || 'N/A'}</p>
                    ${movie.genre ? `<p><strong>Thể loại:</strong> <span class="movie-card-genre">${movie.genre}</span></p>` : ''}
                    ${movie.releaseDate ? `<p><strong>Ngày phát hành:</strong> ${formatDate(movie.releaseDate)}</p>` : ''}
                    ${movie.price != null ? `<p><strong>Giá:</strong> <span class="movie-card-price">${formatPriceToUSD(movie.price)}</span></p>` : '<p><strong>Giá:</strong> <span class="movie-card-price-unset">Chưa đặt</span></p>'}
                    ${movie.directorName ? `<p><strong>Đạo diễn:</strong> ${getUniqueNamesFromString(movie.directorName)}</p>` : ''}
                    ${movie.studioName ? `<p><strong>Studio:</strong> ${getUniqueNamesFromString(movie.studioName)}</p>` : ''}
                    <div class="movie-card-actors">
                        <strong>Diễn viên:</strong>
                        ${movie.actors && movie.actors.length > 0
                            ? movie.actors.map(actor => actor.name || 'Ẩn danh').join(', ')
                            : 'Chưa có'}
                    </div>
                </div>
                <div class="movie-card-actions">
                    <form action="/customer/orders/create" method="get" class="order-form">
                        <input type="hidden" name="movieId" value="${movie.movieID || ''}" />
                        <button type="submit" class="btn btn-success">
                            <i class="fas fa-shopping-bag"></i> Đặt mua
                        </button>
                    </form>
                </div>
            </div>
        </div>
    `).join('');
}

// Hiển thị thông báo lỗi cho phim
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

// Cập nhật số lượng kết quả
function updateResultsCount(genre, searchText) {
    const count = currentMovies.length;
    const genreText = genre ? ` trong "${genre}"` : '';
    const searchTextDisplay = searchText ? ` khớp với "${searchText}"` : '';
    resultsCount.textContent = `Tìm thấy ${count} phim${count !== 1 ? '' : ''}${genreText}${searchTextDisplay}`;
    resultsCount.style.display = 'block';
}

// Hiển thị/ẩn loading
function showLoading(show) {
    loadingContainer.style.display = show ? 'block' : 'none';
    moviesList.style.display = show ? 'none' : 'grid';
}

// Chức năng tìm kiếm và lọc thể loại
searchInput.addEventListener('input', applyFilters);
genreSelect.addEventListener('change', function() {
    const selectedGenre = this.value;
    loadMoviesFromBackend(selectedGenre, searchInput.value); // Re-fetch dựa trên bộ lọc
});
