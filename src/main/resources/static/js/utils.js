function showToast(message, status = 'bg-danger') {
    const toast = document.getElementById('liveToast');
    const toastMessage = document.getElementById('toastMessage');
    const toastWrapper = document.getElementById('toastWrapper');

    if (toast && toastMessage && toastWrapper) {
        toastMessage.textContent = message;
        toast.className = `toast align-items-center text-white border-0 ${status}`;
        toastWrapper.style.display = 'block';
        const bootstrapToast = new bootstrap.Toast(toast, { delay: 3000 });
        bootstrapToast.show();
    }
}

let page = 1;
let loading = false;
let hasMore = true;
let filterChanged = false;
let searchString = null;
let sorting = {sortByField:'publishedAt', order:'desc'}
let filtering = {authorId:'0', publishedAt:'', tagIds:[],
    sortByField:'publishedAt', order:'desc'}
let previousFiltering = { ...filtering };

document.addEventListener("DOMContentLoaded", function () {
    let lastScrollTop = 0;
    const navbar = document.getElementById("mainNavbar");
    window.addEventListener("scroll", function () {
      const currentScroll = window.pageYOffset || document.documentElement.scrollTop;
      if (currentScroll <= 0) {
        navbar.classList.remove("navbar-hidden");
        navbar.classList.add("navbar-visible");
        return;
      }
      if (currentScroll > lastScrollTop) {
        // Scrolling down
        navbar.classList.remove("navbar-visible");
        navbar.classList.add("navbar-hidden");
      } else {
        // Scrolling up
        navbar.classList.remove("navbar-hidden");
        navbar.classList.add("navbar-visible");
      }
      lastScrollTop = currentScroll;
    });

    const tagSidebar = document.getElementById('tagSidebar');
    const dropIcon = document.getElementById('collapseIcon');
    if (tagSidebar && dropIcon) {
        tagSidebar.addEventListener('show.bs.collapse', () => {
            dropIcon.classList.remove('fa-chevron-down');
            dropIcon.classList.add('fa-chevron-up');
        });
        tagSidebar.addEventListener('hide.bs.collapse', () => {
            dropIcon.classList.remove('fa-chevron-up');
            dropIcon.classList.add('fa-chevron-down');
        });
    }

    const lazyLoaderPostContainer = document.getElementById('lazyLoaderAndContainer');
    const loadingIndicator = document.getElementById('loadingIndicator');

    if (lazyLoaderPostContainer) {
        lazyLoaderPostContainer.addEventListener('scroll', function () {
            const { scrollTop, scrollHeight, clientHeight } = this;
            if (scrollTop + clientHeight >= scrollHeight - 50 && !loading) {
                loadMorePosts(loadingIndicator, lazyLoaderPostContainer)
                    .finally(() => {
                        loading = false;
                    });
            }
        });
        lazyLoaderPostContainer.addEventListener("click", function (event) {
            const savePost = event.target.closest('.save-post-link');
            if (!savePost) return;
            event.preventDefault();
            savePostForUser(savePost)
        });
    }

    const filterForm = document.getElementById('filterForm');
    if (filterForm) {
        filterForm.addEventListener("submit", function (event) {
            event.preventDefault();
            const modalElement = document.getElementById('filterSidebar');
            const modal = bootstrap.Modal.getInstance(modalElement);
            const formData = new FormData(filterForm);
            const sortOrder = document.querySelector('input[name="sortOrder"]:checked')?.value;
            const selectedTagIds = Array.from(document.querySelectorAll(".tag-checkbox:checked"))
                  .map(cb => parseInt(cb.value, 10));
            const newFiltering = {
                authorId: formData.get('authorId'),
                publishedAt: formData.get('publishedAt'),
                tagIds: selectedTagIds,
                sortByField: formData.get('sortField'),
                order: sortOrder
            }
            sorting.sortByField = formData.get('sortField');
            sorting.order = sortOrder;
            filterChanged = filtersChanged(previousFiltering, newFiltering)
            if (filterChanged) {
                page = 0;
                hasMore = true;
                previousFiltering = { ...newFiltering, tagIds: [...newFiltering.tagIds] };
            } else {
                modal.hide();
                return;
            }
            filtering = newFiltering;
            modal.hide();
            loadMorePosts(loadingIndicator, lazyLoaderPostContainer);
        });
    }

    const sortForm = document.getElementById('sortForm');
        if (sortForm) {
            sortForm.addEventListener("submit", function (event) {
                event.preventDefault();
                page = 0;
                const sortDirection = document.getElementById('sortDirection');
                const formData = new FormData(sortForm);
                sorting.sortByField = formData.get('sortField');
                sorting.order = formData.get('order');
                if(sorting.order === 'asc') {
                    sortDirection.classList.remove('bi-arrow-down')
                    sortDirection.classList.add('bi-arrow-up')
                } else {
                    sortDirection.classList.remove('bi-arrow-up')
                    sortDirection.classList.add('bi-arrow-down')
                }
                const sortOrderInput = sortForm.querySelector('[name="order"]');
                const currentOrder = sortOrderInput.value;
                const newOrder = currentOrder === 'asc' ? 'desc' : 'asc';
                sortOrderInput.value = newOrder;
                hasMore = true;
                filterChanged = true
                loadMorePosts(loadingIndicator, lazyLoaderPostContainer);
            });
        }

    const searchInputs = ["searchInput", "searchInput2"];
    const searchForms = ["searchForm", "searchForm2"];
    searchInputs.forEach(id => {
        const searchInput = document.getElementById(id);
        if (searchInput) {
            searchInput.addEventListener("input", function() {
                searchString = this.value.trim();
                hasMore = true;
            });
        }
    });

    searchForms.forEach((id, index) => {
        const searchForm = document.getElementById(id);
        if (searchForm) {
            searchForm.addEventListener("submit", function(e) {
                e.preventDefault();
                page = 0;
                const input = document.getElementById(searchInputs[index]);
                const query = input?.value.trim();
                if (query.trim()) {
                    filterChanged = true
                    loadMorePosts(loadingIndicator, lazyLoaderPostContainer);
                }
            });
        }
    });

    document.querySelectorAll('.likes-count').forEach(el => {
        const rawCount = parseInt(el.dataset.rawCount);
        el.textContent = formatLikeCount(rawCount);
    });

    loadFiltersFromInputs();
});

function formatLikeCount(count) {
    if (count >= 1_000_000) {
        return (count / 1_000_000).toFixed(count % 1_000_000 === 0 ? 0 : 1) + 'M';
    } else if (count >= 1_000) {
        return (count / 1_000).toFixed(count % 1_000 === 0 ? 0 : 1) + 'k';
    }
    return count.toString();
}

const loadMorePosts = async (loadingIndicator, lazyLoaderPostContainer) => {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    try {
        if (loading || !hasMore) return;
        loading = true;
        loadingIndicator.classList.add("d-block");
        loadingIndicator.classList.remove("d-none");

        const requestBody = {
            limit: 10,
            offset: ++page,
            sortByField: filtering.sortByField,
            orderBy: filtering.order,
            publishedAt: filtering.publishedAt,
            authorId: filtering.authorId,
            tagIds: filtering.tagIds,
            searchSting: searchString
        };
        console.log(requestBody)
        const response = await fetch(`/blog/posts/api`, {
            method: 'POST',
            body: JSON.stringify(requestBody),
            headers: {
                "Content-Type": "application/json",
                [header]: token
            }
        });
        if (response.ok) {
            const html = await response.text();

            if (html.trim() === '<div></div>') {
                if (searchString && searchString.trim() != '') {
                    showToast("No result found for searched text.", 'bg-danger');
                    return;
                }
                hasMore = false;
                loading = false;
            } else {
                const tempDiv = document.createElement('div');
                tempDiv.innerHTML = html;
                const newPosts = tempDiv.children;
                if (filterChanged) {
                    lazyLoaderPostContainer.innerHTML = '';
                    filterChanged = false
                }
                Array.from(newPosts).forEach(post => lazyLoaderPostContainer.appendChild(post));
            }
        }
    } catch (err) {
//        if (!navigator.onLine) {
//            showToast("You're offline. Please check your internet connection.", 'bg-warning');
//        } else
        if (err.message.includes('Failed to fetch') || err.message.includes('ERR_CONNECTION_REFUSED')) {
            showToast("Cannot connect to server.", 'bg-danger');
        } else {
            showToast("Error fetching posts.", 'bg-danger');
        }
    } finally {
        loading = false;
        loadingIndicator.classList.remove("d-block");
        loadingIndicator.classList.add("d-none");
    }
};


function filtersChanged(oldFilter, newFilter) {
    return (
        oldFilter.authorId !== newFilter.authorId ||
        oldFilter.publishedAt !== newFilter.publishedAt ||
        oldFilter.tagIds.length !== newFilter.tagIds.length ||
        !oldFilter.tagIds.every((id, index) => id === newFilter.tagIds[index]) ||
        oldFilter.sortByField !== newFilter.sortByField ||
        oldFilter.order !== newFilter.order
    );
}

function savePostForUser (savePost) {
    if (!savePost) return;
    const postId = savePost.getAttribute('data-id');
    const formData = new FormData();
    formData.set("postId", postId);
    let colRm;
    let colAdd;
    initApiCall("/blog/posts/api/save-post", {
        method: 'POST',
        body: formData
    })
    .then(data => {
        if (data.success) {
            if(data.data.liked){
                colAdd = ("text-primary");
                colRm = ("text-muted");
            } else {
                colRm = ("text-primary");
                colAdd = ("text-muted");
            }
            document.querySelectorAll(`.save-post-link[data-id="${postId}"]`).forEach(el => {
                el.classList.remove(colRm);
                el.classList.add(colAdd);
            });
            showToast(data.message, 'bg-success');
        } else {
            showToast(data.message, 'bg-danger')
        }
    })
    .catch(error => {showToast('Something went wrong!', 'bg-danger')});
}

const initApiCall  = async (url, {
       method = 'GET',
       headers = {},
       query = {},
       body = null
     } = {}) => {
    try {
        const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
        const queryString = new URLSearchParams(query).toString();
        const fullUrl = queryString ? `${url}?${queryString}` : url;
        const isFormData = body instanceof FormData;
        const effectiveMethod = isFormData && method.toUpperCase() === 'GET' ? 'POST' : method.toUpperCase();
        const finalHeaders = isFormData
            ? {[header]: token, ...headers}
            : {
              'Content-Type': 'application/json',
              [header]: token,
              ...headers
            };
        const fetchOptions = {
              method: effectiveMethod,
              headers: finalHeaders
            };
        if (body && !['GET', 'HEAD'].includes(effectiveMethod)) {
          fetchOptions.body = isFormData ? body : JSON.stringify(body);
        }
        const response = await fetch(url, fetchOptions);
        if (!response.ok) {
          const errorBody = await response.text();
          throw new Error(`HTTP ${response.status} - ${response.statusText}: ${errorBody}`);
        }
        const contentType = response.headers.get('content-type');
        return contentType && contentType.includes('application/json')
          ? await response.json()
          : await response.text();
    } catch (error) {
        showToast(error.message, 'bg-danger');
    }
}


function loadFiltersFromInputs() {
  const authorEl = document.querySelector('#authorId');
  const publishedAtEl = document.querySelector('#publishedAt');
  const sortFieldEl = document.querySelector('#sortField');
  const sortOrderEl = document.querySelector('input[name="sortOrder"]:checked');
  const tagElements = document.querySelectorAll('.tag-checkbox:checked');

  filtering.authorId = authorEl ? authorEl.value : '0';
  filtering.publishedAt = publishedAtEl ? publishedAtEl.value : '';
  filtering.sortByField = sortFieldEl ? sortFieldEl.value : 'publishedAt';
  filtering.order = sortOrderEl ? sortOrderEl.value : 'desc';

  filtering.tagIds = tagElements.length > 0
    ? Array.from(tagElements).map(cb => parseInt(cb.value, 10))
    : [];
}