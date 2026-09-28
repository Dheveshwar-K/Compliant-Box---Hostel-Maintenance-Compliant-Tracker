/* =========================================================
   API
========================================================= */

const API = {
    complaints: "/api/complaints",
    hostellers: "/api/hostellers",
    staff: "/api/staff",
    categories: "/api/categories"
};


/* =========================================================
   COMMON API HELPER
========================================================= */

async function apiRequest(url, options = {}) {

    try {

        const response = await fetch(url, {

            headers: {
                "Content-Type": "application/json",
                ...(options.headers || {})
            },

            ...options

        });

        const contentType =
            response.headers.get("content-type") || "";

        let data;

        if (contentType.includes("application/json")) {

            data = await response.json();

        } else {

            data = await response.text();

        }


        if (!response.ok) {

            console.error("API ERROR:", {
                url: url,
                status: response.status,
                response: data
            });

            let message =
                `Request failed: ${response.status}`;

            if (
                typeof data === "object" &&
                data !== null
            ) {

                message =
                    data.message ||
                    data.error ||
                    data.detail ||
                    JSON.stringify(data);

            } else if (data) {

                message = data;

            }

            throw new Error(message);
        }


        return data;

    } catch (error) {

        console.error("REQUEST ERROR:", error);

        showToast(
            error.message ||
            "Something went wrong",
            "error"
        );

        throw error;
    }
}


/* =========================================================
   TOAST
========================================================= */

function showToast(
    message,
    type = "success"
) {

    const oldToast =
        document.querySelector(".toast");

    if (oldToast) {
        oldToast.remove();
    }


    const toast =
        document.createElement("div");

    toast.className =
        `toast ${type}`;

    toast.textContent =
        message;

    document.body.appendChild(toast);


    setTimeout(() => {

        toast.classList.add("hide");

        setTimeout(() => {

            toast.remove();

        }, 300);

    }, 2500);
}


/* =========================================================
   LOGIN
========================================================= */

function initializeLogin() {

    const loginForm =
        document.getElementById("loginForm");

    if (!loginForm) {
        return;
    }


    loginForm.addEventListener(
        "submit",
        function (event) {

            event.preventDefault();

            const roleElement =
                document.getElementById("loginRole");

            if (!roleElement) {
                return;
            }

            const role =
                roleElement.value;


            console.log(
                "Login button clicked"
            );

            console.log(
                "Selected role:",
                role
            );


            if (role === "resident") {

                window.location.href =
                    "/complaints";

            } else if (role === "staff") {

                window.location.href =
                    "/complaints";

            } else if (role === "warden") {

                window.location.href =
                    "/dashboard";
            }
        }
    );
}


/* =========================================================
   DASHBOARD
========================================================= */

async function loadDashboard() {

    const totalElement =
        document.getElementById(
            "totalComplaints"
        );

    if (!totalElement) {
        return;
    }


    try {

        const complaints =
            await apiRequest(
                API.complaints
            );


        const overdue =
            await apiRequest(
                `${API.complaints}/overdue`
            );


        const open =
            complaints.filter(
                c =>
                    normalizeStatus(c.status)
                    === "OPEN"
            );


        const progress =
            complaints.filter(
                c =>
                    normalizeStatus(c.status)
                    === "IN_PROGRESS"
            );


        document.getElementById(
            "totalComplaints"
        ).textContent =
            complaints.length;


        const openElement =
            document.getElementById(
                "openComplaints"
            );

        if (openElement) {

            openElement.textContent =
                open.length;
        }


        const progressElement =
            document.getElementById(
                "progressComplaints"
            );

        if (progressElement) {

            progressElement.textContent =
                progress.length;
        }


        const overdueElement =
            document.getElementById(
                "overdueComplaints"
            );

        if (overdueElement) {

            overdueElement.textContent =
                overdue.length;
        }


        renderRecentComplaints(
            complaints
        );

        renderOverdue(
            overdue
        );

    } catch (error) {

        console.error(
            "Dashboard error:",
            error
        );
    }
}


/* =========================================================
   RECENT COMPLAINTS
========================================================= */

function renderRecentComplaints(
    complaints
) {

    const table =
        document.getElementById(
            "recentComplaints"
        );

    if (!table) {
        return;
    }


    if (!complaints.length) {

        table.innerHTML = `
            <tr>
                <td
                    colspan="5"
                    class="empty-state">
                    No complaints found
                </td>
            </tr>
        `;

        return;
    }


    const recent =
        [...complaints]
            .sort(
                (a, b) =>
                    new Date(
                        b.createdAt || 0
                    ) -
                    new Date(
                        a.createdAt || 0
                    )
            )
            .slice(0, 8);


    table.innerHTML =
        recent.map(c => `

            <tr>

                <td>
                    #${c.id}
                </td>

                <td>
                    ${c.category?.name || "N/A"}
                </td>

                <td>
                    ${c.roomNumber || "N/A"}
                </td>

                <td>
                    ${statusBadge(c.status)}
                </td>

                <td>
                    ${formatDate(c.createdAt)}
                </td>

            </tr>

        `).join("");
}


/* =========================================================
   OVERDUE COMPLAINTS
========================================================= */

function renderOverdue(
    complaints
) {

    const container =
        document.getElementById(
            "overdueList"
        );

    if (!container) {
        return;
    }


    if (!complaints.length) {

        container.innerHTML = `
            <div class="empty-state">
                No overdue complaints 🎉
            </div>
        `;

        return;
    }


    container.innerHTML =
        complaints
            .slice(0, 5)
            .map(c => `

                <div class="quick-action">

                    <strong>
                        #${c.id}
                        -
                        ${c.category?.name || "Complaint"}
                    </strong>

                    <small>
                        Room:
                        ${c.roomNumber || "N/A"}
                    </small>

                    <div style="margin-top:8px">
                        ${statusBadge(
                            c.status,
                            true
                        )}
                    </div>

                </div>

            `)
            .join("");
}


/* =========================================================
   COMPLAINTS
========================================================= */

let allComplaints = [];


async function loadComplaints() {

    const table =
        document.getElementById(
            "complaintTable"
        );

    if (!table) {
        return;
    }


    try {

        allComplaints =
            await apiRequest(
                API.complaints
            );


        renderComplaints(
            allComplaints
        );


        await loadCategoriesIntoSelect();

    } catch (error) {

        console.error(
            "Failed to load complaints:",
            error
        );


        table.innerHTML = `
            <tr>
                <td
                    colspan="7"
                    class="empty-state">
                    Failed to load complaints
                </td>
            </tr>
        `;
    }
}


/* =========================================================
   RENDER COMPLAINTS
========================================================= */

function renderComplaints(
    complaints
) {

    const table =
        document.getElementById(
            "complaintTable"
        );

    if (!table) {
        return;
    }


    if (!complaints.length) {

        table.innerHTML = `
            <tr>
                <td
                    colspan="7"
                    class="empty-state">
                    No complaints found
                </td>
            </tr>
        `;

        return;
    }


    table.innerHTML =
        complaints.map(c => `

            <tr>

                <td>
                    #${c.id}
                </td>

                <td>
                    ${c.category?.name || "N/A"}
                </td>

                <td>
                    ${c.roomNumber || "N/A"}
                </td>

                <td>
                    ${truncate(
                        c.description,
                        45
                    )}
                </td>

                <td>
                    ${statusBadge(c.status)}
                </td>

                <td>
                    ${formatDate(c.createdAt)}
                </td>

                <td>

                    <button
                        class="btn btn-secondary"
                        style="padding:7px 10px"
                        onclick="viewComplaint(${c.id})">

                        View

                    </button>

                    <button
                        class="btn btn-secondary"
                        style="padding:7px 10px; margin-left:5px"
                        onclick="deleteComplaint(${c.id})">

                        Delete

                    </button>

                </td>

            </tr>

        `).join("");
}


/* =========================================================
   FILTER COMPLAINTS
========================================================= */

function filterComplaints() {

    const search =
        document.getElementById(
            "complaintSearch"
        )?.value
            .toLowerCase() || "";


    const status =
        document.getElementById(
            "statusFilter"
        )?.value || "";


    const filtered =
        allComplaints.filter(c => {

            const text =
                `${c.id}
                 ${c.description || ""}
                 ${c.roomNumber || ""}
                 ${c.category?.name || ""}`
                    .toLowerCase();


            const matchesSearch =
                text.includes(search);


            const matchesStatus =
                !status ||
                normalizeStatus(c.status)
                === status;


            return (
                matchesSearch &&
                matchesStatus
            );
        });


    renderComplaints(
        filtered
    );
}


/* =========================================================
   CREATE COMPLAINT
========================================================= */

function initializeComplaintForm() {

    const complaintForm =
        document.getElementById(
            "complaintForm"
        );

    if (!complaintForm) {
        return;
    }


    console.log(
        "Complaint form loaded successfully"
    );


    complaintForm.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();


            console.log(
                "SUBMIT COMPLAINT CLICKED"
            );


            const categoryElement =
                document.getElementById(
                    "complaintCategory"
                );


            const roomElement =
                document.getElementById(
                    "complaintRoom"
                );


            const descriptionElement =
                document.getElementById(
                    "complaintDescription"
                );


            if (
                !categoryElement ||
                !roomElement ||
                !descriptionElement
            ) {

                console.error(
                    "Complaint form fields not found"
                );

                return;
            }


            const categoryId =
                categoryElement.value;


            const roomNumber =
                roomElement.value.trim();


            const description =
                descriptionElement.value.trim();


            console.log(
                "Complaint data:",
                {
                    categoryId,
                    roomNumber,
                    description
                }
            );


            if (!categoryId) {

                alert(
                    "Please select a category"
                );

                return;
            }


            if (!roomNumber) {

                alert(
                    "Please enter room number"
                );

                return;
            }


            if (!description) {

                alert(
                    "Please enter complaint description"
                );

                return;
            }


            try {

                const complaintData = {

                    category: {
                        id: Number(categoryId)
                    },

                    roomNumber:
                        roomNumber,

                    description:
                        description
                };


                console.log(
                    "Sending complaint:",
                    complaintData
                );


                await apiRequest(
                    API.complaints,
                    {
                        method: "POST",

                        body:
                            JSON.stringify(
                                complaintData
                            )
                    }
                );


                showToast(
                    "Complaint created successfully"
                );


                complaintForm.reset();


                closeComplaintModal();


                await loadComplaints();


            } catch (error) {

                console.error(
                    "Complaint creation failed:",
                    error
                );

                alert(
                    "Failed to create complaint: " +
                    error.message
                );
            }
        }
    );
}


/* =========================================================
   DELETE COMPLAINT
========================================================= */

async function deleteComplaint(id) {

    if (
        !confirm(
            "Delete this complaint?"
        )
    ) {
        return;
    }


    try {

        await apiRequest(
            `${API.complaints}/${id}`,
            {
                method: "DELETE"
            }
        );


        showToast(
            "Complaint deleted successfully"
        );


        await loadComplaints();


    } catch (error) {

        console.error(
            "Failed to delete complaint:",
            error
        );
    }
}


/* =========================================================
   COMPLAINT MODAL
========================================================= */

function openComplaintModal() {

    const modal =
        document.getElementById(
            "complaintModal"
        );

    if (modal) {

        modal.classList.add("show");
    }
}


function closeComplaintModal() {

    const modal =
        document.getElementById(
            "complaintModal"
        );

    if (modal) {

        modal.classList.remove("show");
    }
}


/* =========================================================
   CATEGORIES
========================================================= */

async function loadCategories() {

    const grid =
        document.getElementById(
            "categoryGrid"
        );

    if (!grid) {
        return;
    }


    try {

        const categories =
            await apiRequest(
                API.categories
            );


        if (!categories.length) {

            grid.innerHTML = `
                <div class="empty-state">
                    No categories found
                </div>
            `;

            return;
        }


        grid.innerHTML =
            categories.map(c => `

                <div class="category-card">

                    <div class="category-icon">
                        #
                    </div>

                    <h3>
                        ${c.name}
                    </h3>

                    <p>
                        ${
                            c.description ||
                            "No description"
                        }
                    </p>

                </div>

            `).join("");


    } catch (error) {

        console.error(
            "Failed to load categories:",
            error
        );


        grid.innerHTML = `
            <div class="empty-state">
                Failed to load categories
            </div>
        `;
    }
}


/* =========================================================
   LOAD CATEGORIES INTO COMPLAINT SELECT
========================================================= */

async function loadCategoriesIntoSelect() {

    const select =
        document.getElementById(
            "complaintCategory"
        );

    if (!select) {
        return;
    }


    try {

        const categories =
            await apiRequest(
                API.categories
            );


        select.innerHTML = `
            <option value="">
                Select category
            </option>
        `;


        categories.forEach(c => {

            select.innerHTML += `
                <option value="${c.id}">
                    ${c.name}
                </option>
            `;
        });


    } catch (error) {

        console.error(
            "Failed to load categories:",
            error
        );
    }
}


/* =========================================================
   CATEGORY MODAL
========================================================= */

function openCategoryModal() {

    document
        .getElementById("categoryModal")
        ?.classList.add("show");
}


function closeCategoryModal() {

    document
        .getElementById("categoryModal")
        ?.classList.remove("show");
}


/* =========================================================
   CATEGORY FORM
========================================================= */

function initializeCategoryForm() {

    const categoryForm =
        document.getElementById(
            "categoryForm"
        );

    if (!categoryForm) {
        return;
    }


    categoryForm.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();


            const name =
                document.getElementById(
                    "categoryName"
                ).value.trim();


            const description =
                document.getElementById(
                    "categoryDescription"
                ).value.trim();


            if (!name) {

                alert(
                    "Please enter category name"
                );

                return;
            }


            try {

                await apiRequest(
                    API.categories,
                    {
                        method: "POST",

                        body:
                            JSON.stringify({
                                name,
                                description
                            })
                    }
                );


                showToast(
                    "Category created successfully"
                );


                closeCategoryModal();


                await loadCategories();

                await loadCategoriesIntoSelect();


                categoryForm.reset();


            } catch (error) {

                console.error(
                    "Category creation failed:",
                    error
                );
            }
        }
    );
}


/* =========================================================
   RESIDENTS
========================================================= */

async function loadResidents() {

    const table =
        document.getElementById(
            "residentTable"
        );

    if (!table) {
        return;
    }


    try {

        const residents =
            await apiRequest(
                API.hostellers
            );


        if (!residents.length) {

            table.innerHTML = `
                <tr>
                    <td
                        colspan="4"
                        class="empty-state">
                        No residents found
                    </td>
                </tr>
            `;

            return;
        }


        table.innerHTML =
            residents.map(r => `

                <tr>

                    <td>
                        #${r.id}
                    </td>

                    <td>
                        ${r.name}
                    </td>

                    <td>
                        ${r.roomNumber}
                    </td>

                    <td>

                        <button
                            class="btn btn-secondary"
                            style="padding:7px 10px"
                            onclick="deleteResident(${r.id})">

                            Delete

                        </button>

                    </td>

                </tr>

            `).join("");


    } catch (error) {

        console.error(
            "Failed to load residents:",
            error
        );
    }
}


/* =========================================================
   RESIDENT MODAL
========================================================= */

function openResidentModal() {

    document
        .getElementById("residentModal")
        ?.classList.add("show");
}


function closeResidentModal() {

    document
        .getElementById("residentModal")
        ?.classList.remove("show");
}


/* =========================================================
   RESIDENT FORM
========================================================= */

function initializeResidentForm() {

    const residentForm =
        document.getElementById(
            "residentForm"
        );

    if (!residentForm) {
        return;
    }


    residentForm.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();


            const name =
                document.getElementById(
                    "residentName"
                ).value.trim();


            const roomNumber =
                document.getElementById(
                    "residentRoom"
                ).value.trim();


            if (!name) {

                alert(
                    "Please enter resident name"
                );

                return;
            }


            if (!roomNumber) {

                alert(
                    "Please enter room number"
                );

                return;
            }


            try {

                await apiRequest(
                    API.hostellers,
                    {
                        method: "POST",

                        body:
                            JSON.stringify({
                                name,
                                roomNumber
                            })
                    }
                );


                showToast(
                    "Resident added successfully"
                );


                closeResidentModal();


                residentForm.reset();


                await loadResidents();


            } catch (error) {

                console.error(
                    "Resident creation failed:",
                    error
                );
            }
        }
    );
}


/* =========================================================
   DELETE RESIDENT
========================================================= */

async function deleteResident(id) {

    if (
        !confirm(
            "Delete this resident?"
        )
    ) {
        return;
    }


    try {

        await apiRequest(
            `${API.hostellers}/${id}`,
            {
                method: "DELETE"
            }
        );


        showToast(
            "Resident deleted successfully"
        );


        await loadResidents();


    } catch (error) {

        console.error(
            "Failed to delete resident:",
            error
        );
    }
}


/* =========================================================
   STAFF
========================================================= */

async function loadStaff() {

    const table =
        document.getElementById(
            "staffTable"
        );

    if (!table) {
        return;
    }


    try {

        const staff =
            await apiRequest(
                API.staff
            );


        if (!staff.length) {

            table.innerHTML = `
                <tr>
                    <td
                        colspan="5"
                        class="empty-state">
                        No staff found
                    </td>
                </tr>
            `;

            return;
        }


        table.innerHTML =
            staff.map(s => `

                <tr>

                    <td>
                        #${s.id}
                    </td>

                    <td>
                        ${s.name}
                    </td>

                    <td>
                        ${s.role}
                    </td>

                    <td>

                        <span
                            class="status status-resolved">

                            ACTIVE

                        </span>

                    </td>

                    <td>

                        <button
                            class="btn btn-secondary"
                            style="padding:7px 10px"
                            onclick="deleteStaff(${s.id})">

                            Delete

                        </button>

                    </td>

                </tr>

            `).join("");


    } catch (error) {

        console.error(
            "Failed to load staff:",
            error
        );
    }
}


/* =========================================================
   STAFF MODAL
========================================================= */

function openStaffModal() {

    document
        .getElementById("staffModal")
        ?.classList.add("show");
}


function closeStaffModal() {

    document
        .getElementById("staffModal")
        ?.classList.remove("show");
}


/* =========================================================
   STAFF FORM
========================================================= */

function initializeStaffForm() {

    const staffForm =
        document.getElementById(
            "staffForm"
        );

    if (!staffForm) {
        return;
    }


    staffForm.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();


            const name =
                document.getElementById(
                    "staffName"
                ).value.trim();


            const role =
                document.getElementById(
                    "staffRole"
                ).value;


            if (!name) {

                alert(
                    "Please enter staff name"
                );

                return;
            }


            if (!role) {

                alert(
                    "Please select staff role"
                );

                return;
            }


            try {

                await apiRequest(
                    API.staff,
                    {
                        method: "POST",

                        body:
                            JSON.stringify({
                                name,
                                role
                            })
                    }
                );


                showToast(
                    "Staff added successfully"
                );


                closeStaffModal();


                staffForm.reset();


                await loadStaff();


            } catch (error) {

                console.error(
                    "Staff creation failed:",
                    error
                );
            }
        }
    );
}


/* =========================================================
   DELETE STAFF
========================================================= */

async function deleteStaff(id) {

    if (
        !confirm(
            "Delete this staff member?"
        )
    ) {
        return;
    }


    try {

        await apiRequest(
            `${API.staff}/${id}`,
            {
                method: "DELETE"
            }
        );


        showToast(
            "Staff deleted successfully"
        );


        await loadStaff();


    } catch (error) {

        console.error(
            "Failed to delete staff:",
            error
        );
    }
}


/* =========================================================
   HELPERS
========================================================= */

function normalizeStatus(status) {

    if (!status) {
        return "";
    }


    return status
        .toString()
        .toUpperCase()
        .replace("-", "_")
        .replace(" ", "_");
}


function statusBadge(
    status,
    overdue = false
) {

    const normalized =
        normalizeStatus(status);


    if (overdue) {

        return `
            <span class="status status-overdue">
                OVERDUE
            </span>
        `;
    }


    if (normalized === "OPEN") {

        return `
            <span class="status status-open">
                OPEN
            </span>
        `;
    }


    if (
        normalized === "IN_PROGRESS"
    ) {

        return `
            <span class="status status-progress">
                IN PROGRESS
            </span>
        `;
    }


    if (
        normalized === "RESOLVED"
    ) {

        return `
            <span class="status status-resolved">
                RESOLVED
            </span>
        `;
    }


    return `
        <span class="status status-open">
            ${status || "UNKNOWN"}
        </span>
    `;
}


function formatDate(date) {

    if (!date) {
        return "-";
    }


    try {

        return new Date(date)
            .toLocaleDateString(
                "en-IN",
                {
                    day: "2-digit",
                    month: "short",
                    year: "numeric"
                }
            );

    } catch (error) {

        return date;
    }
}


function truncate(
    text,
    length
) {

    if (!text) {
        return "-";
    }


    if (text.length <= length) {
        return text;
    }


    return (
        text.substring(0, length) +
        "..."
    );
}


/* =========================================================
   VIEW COMPLAINT
========================================================= */

async function viewComplaint(id) {

    try {

        const complaint =
            await apiRequest(
                `${API.complaints}/${id}`
            );


        alert(
            `Complaint #${complaint.id}\n\n` +

            `Category: ${
                complaint.category?.name ||
                "N/A"
            }\n` +

            `Room: ${
                complaint.roomNumber ||
                "N/A"
            }\n` +

            `Status: ${
                complaint.status ||
                "N/A"
            }\n\n` +

            `Description:\n${
                complaint.description ||
                "N/A"
            }`
        );


    } catch (error) {

        console.error(
            "Failed to view complaint:",
            error
        );
    }
}


/* =========================================================
   PAGE INITIALIZATION
========================================================= */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        console.log(
            "ComplaintBox JavaScript loaded"
        );


        initializeLogin();

        initializeComplaintForm();

        initializeCategoryForm();

        initializeResidentForm();

        initializeStaffForm();


        loadDashboard();

        loadComplaints();

        loadResidents();

        loadStaff();

        loadCategories();

    }
);