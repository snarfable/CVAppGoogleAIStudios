package com.example.model

data class ExperienceItem(
    val id: String,
    val role: String,
    val company: String,
    val location: String,
    val period: String,
    val summary: String,
    val bulletPoints: List<String>,
    val tags: List<String>,
    val architectureNote: String,
    val codeSnippetTitle: String,
    val codeSnippet: String
)

data class ProjectItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val metrics: List<Pair<String, String>>,
    val techStack: List<String>,
    val keyInnovation: String
)

data class SkillCategory(
    val title: String,
    val iconName: String,
    val skills: List<SkillItem>
)

data class SkillItem(
    val name: String,
    val levelPercent: Int,
    val highlight: String
)

data class ProjectCategoryData(
    val id: String,
    val name: String,
    val projectCount: Int,
    val experiencePercentage: Int,
    val proficiencyScore: Int,
    val color: androidx.compose.ui.graphics.Color,
    val keyTech: List<String>
)

data class SkillMetric(
    val name: String,
    val category: String,
    val score: Int,
    val yearsExp: Double,
    val highlight: String,
    val accentColor: androidx.compose.ui.graphics.Color
)

object CareerRepository {
    val profile = mapOf(
        "name" to "Ryan P. Daly",
        "headline" to "Machine Learning & Edge AI Systems Engineer",
        "pillars" to "High-Performance Scientific Computing • Computer Vision & Perception • Physics-Informed Simulation & HIL/CHIL • Low-Latency Edge Inference",
        "email" to "ryan.p.daly2@gmail.com",
        "phone" to "+1 (401) 753-9046",
        "github1" to "github.com/mathemaphysics",
        "github2" to "github.com/snarfable",
        "summary" to "Machine Learning & AI Software Engineer with a deep technical foundation in high-performance computing, numerical optimization, and computer vision. Proven track record designing and training custom neural network architectures, building synthetic data generation pipelines, and deploying low-latency AI models to embedded and edge hardware. Expert in C++ and Python, with specialized experience bridging large-scale simulation frameworks (HIL/CHIL) with real-time inference and spatial sensor processing (LiDAR, Ultrasonic, GIS)."
    )

    val experiences = listOf(
        ExperienceItem(
            id = "northrop",
            role = "Embedded Software Engineer",
            company = "Northrop Grumman",
            location = "Defense & Space",
            period = "2026 – Present",
            summary = "Architecting low-latency telemetry pipelines and deterministic inter-board event dispatching for mission-critical edge compute nodes.",
            bulletPoints = listOf(
                "Architect and optimize low-latency event-handling APIs to streamline inter-board communications for high-throughput sensor and mission-critical edge compute nodes.",
                "Debug complex, asynchronous telemetry pipelines and resolve critical messaging anomalies to guarantee deterministic data throughput for downstream analytical modules.",
                "Deploy scalable system infrastructure adhering to strict low-latency compute standards and technical specifications."
            ),
            tags = listOf("C++20", "Embedded Systems", "Deterministic Telemetry", "Event APIs", "Mission-Critical"),
            architectureNote = "Zero-copy shared memory queues with lock-free ring buffers across heterogeneous processor boards, guaranteeing < 50µs jitter.",
            codeSnippetTitle = "Lock-Free Circular Ring Buffer Event Dispatcher (C++20)",
            codeSnippet = """
template <typename Event, size_t Capacity>
class LockFreeEventQueue {
    static_assert((Capacity & (Capacity - 1)) == 0, "Capacity must be power of 2");
    alignas(64) std::atomic<size_t> head_{0};
    alignas(64) std::atomic<size_t> tail_{0};
    std::array<Event, Capacity> buffer_;
public:
    bool push(const Event& item) noexcept {
        const size_t current_tail = tail_.load(std::memory_order_relaxed);
        if (current_tail - head_.load(std::memory_order_acquire) >= Capacity) {
            return false; // Queue full - strict zero-drop telemetry alert
        }
        buffer_[current_tail & (Capacity - 1)] = item;
        tail_.store(current_tail + 1, std::memory_order_release);
        return true;
    }
};
""".trimIndent()
        ),
        ExperienceItem(
            id = "raytheon",
            role = "ML Simulation and Analysis Engineer",
            company = "Raytheon Missiles & Defense (RTX)",
            location = "Aerospace & Defense",
            period = "2023 – 2025",
            summary = "Physics-informed simulation pipelines generating synthetic telemetry for automated guidance models in HIL/CHIL environments.",
            bulletPoints = listOf(
                "Developed physics-informed simulation pipelines generating synthetic navigation, trajectory, and sensor telemetry used to train and validate automated guidance models.",
                "Built high-fidelity Computer-in-the-Loop (CHIL) and Hardware-in-the-Loop (HIL) interfaces, synchronizing high-rate simulated sensor feeds with on-board edge inference environments.",
                "Designed automated validation benchmarks to measure latency, drift, and predictive accuracy of tracking algorithms across multi-variable operational envelopes."
            ),
            tags = listOf("HIL / CHIL", "Physics Simulation", "Synthetic Telemetry", "Automated Guidance", "PyTorch", "C++17"),
            architectureNote = "Synchronized nanosecond precision clock synchronization between real-time simulator chassis and flight computer edge NPU.",
            codeSnippetTitle = "HIL Clock-Synchronized Telemetry Ingestion Loop",
            codeSnippet = """
void CHILBridge::SynchronizedSensorLoop(double step_dt_sec) {
    auto next_tick = std::chrono::steady_clock::now();
    while (sim_active_.load()) {
        TelemetryPacket synthetic_frame = physics_engine_->Step(step_dt_sec);
        // Inject synthetic LiDAR & inertial telemetry to edge NPU
        hil_bus_->TransmitDMA(synthetic_frame.data(), synthetic_frame.size());
        
        next_tick += std::chrono::duration<double>(step_dt_sec);
        std::this_thread::sleep_until(next_tick);
    }
}
""".trimIndent()
        ),
        ExperienceItem(
            id = "herzog",
            role = "Software Engineer III (Computer Vision & ML)",
            company = "Herzog Technologies, Inc. (HTI)",
            location = "Perception & Vision",
            period = "2019 – 2023",
            summary = "Custom YOLO convolutional neural networks on ultrasonic acoustics, multi-threaded C++ LiDAR ray-tracing, and 3D volumetric point-clouds.",
            bulletPoints = listOf(
                "Ultrasonic Anomaly Detection: Designed, trained, and evaluated a custom YOLO-style convolutional neural network targeting ultrasonic signature data, automating real-time fault identification along linear rail infrastructure.",
                "LiDAR Synthetic Data & Ray-Tracing: Authored a multi-threaded C++ ray-tracing engine simulating physical LiDAR device arrays, generating synthetic spatial point clouds to benchmark perception and object-detection models prior to physical field trials.",
                "3D Volumetric Reconstruction: Developed point-cloud processing algorithms to perform automated volumetric estimation from LiDAR survey datasets, dramatically improving processing speed and accuracy over manual spatial analysis.",
                "Geospatial & Vision Pipeline: Built distributed data ETL pipelines ingesting video frames and GIS survey telemetry to automate geospatial annotation and visual asset mapping."
            ),
            tags = listOf("Ultrasonic YOLO", "LiDAR Ray-Tracing", "3D Point Clouds", "Volumetric Meshing", "C++20", "OpenCV"),
            architectureNote = "Transformed 1D acoustic A-Scans into 2D time-frequency spectrogram tensors, feeding an optimized MobileNet-YOLO backbone at 120 FPS on edge hardware.",
            codeSnippetTitle = "Ultrasonic Acoustic Signal to YOLO 2D Tensor Transformation",
            codeSnippet = """
import torch
import torch.nn as nn

class UltrasonicSpectrogramYOLO(nn.Module):
    def __init__(self, num_classes=5):
        super().__init__()
        # Input: [Batch, 1, TimeSteps, TransducerChannels]
        self.feature_extractor = nn.Sequential(
            nn.Conv2d(1, 32, kernel_size=3, stride=1, padding=1),
            nn.BatchNorm2d(32),
            nn.SiLU(),
            nn.MaxPool2d(2, 2),
            # Downsample to spatial acoustic anomaly grid
            nn.Conv2d(32, 64, kernel_size=3, stride=2, padding=1),
            nn.BatchNorm2d(64),
            nn.SiLU()
        )
        self.detector_head = nn.Conv2d(64, 3 * (5 + num_classes), kernel_size=1)
""".trimIndent()
        ),
        ExperienceItem(
            id = "elcotec",
            role = "Embedded Software Engineer",
            company = "Elcotec PTE Ltd.",
            location = "Singapore",
            period = "2025 – 2026",
            summary = "Designed ChannelSON, a lightweight embedded OS and dynamic event dispatcher parsing serial configuration in flash memory for microcontrollers.",
            bulletPoints = listOf(
                "Designed ChannelSON, a lightweight embedded operating system and dynamic event dispatcher parsing serial configuration data directly in flash memory for resource-constrained microcontrollers.",
                "Optimized serial communication protocols and hardware interrupt handlers, laying the groundwork for deterministically running lightweight inference and edge-detection tasks."
            ),
            tags = listOf("ChannelSON RTOS", "Flash Memory Parsing", "Microcontrollers", "Interrupt Handlers", "C"),
            architectureNote = "Zero dynamic heap allocation; serial JSON-like schemas parsed in-place directly from SPI flash sector buffers.",
            codeSnippetTitle = "ChannelSON In-Flash Zero-Copy Serial Parser",
            codeSnippet = """
typedef struct {
    uint16_t event_id;
    uint16_t payload_len;
    const uint8_t *flash_ptr;
} cson_event_t;

int cson_dispatch_flash_event(const uint8_t *flash_sector, size_t max_len) {
    if (flash_sector[0] != CSON_MAGIC_BYTE) return -1;
    cson_event_t ev = {
        .event_id = (flash_sector[1] << 8) | flash_sector[2],
        .payload_len = (flash_sector[3] << 8) | flash_sector[4],
        .flash_ptr = &flash_sector[5]
    };
    return g_event_bus.publish(&ev);
}
""".trimIndent()
        ),
        ExperienceItem(
            id = "uiowa",
            role = "Computational Research Assistant",
            company = "University of Iowa",
            location = "HPC & Scientific Computing",
            period = "2011 – 2019",
            summary = "Formulated numerical algorithms and high-performance computing (HPC) simulations in C++ and Python for non-linear molecular dynamics.",
            bulletPoints = listOf(
                "Formulated numerical algorithms and high-performance computing (HPC) simulations in C++ and Python to analyze non-linear molecular dynamics and multi-body structural heterogeneity.",
                "Processed multi-terabyte simulation datasets using statistical mechanics, multivariate statistical methods, and spatial distribution functions."
            ),
            tags = listOf("HPC", "C++", "Fortran", "Molecular Dynamics", "Multi-Terabyte Datasets", "OpenMPI"),
            architectureNote = "Distributed particle decomposition across hundreds of MPI nodes using spatial octrees and neighbor list caching.",
            codeSnippetTitle = "Non-Linear Pair Potential & Spatial Distribution Computation",
            codeSnippet = """
void compute_radial_distribution(const std::vector<Vec3>& positions,
                                 std::vector<double>& g_r, double dr, double max_r) {
    const size_t n = positions.size();
    #pragma omp parallel for reduction(+:g_r[:])
    for (size_t i = 0; i < n; ++i) {
        for (size_t j = i + 1; j < n; ++j) {
            double dist = (positions[i] - positions[j]).norm();
            if (dist < max_r) {
                int bin = static_cast<int>(dist / dr);
                g_r[bin] += 2.0;
            }
        }
    }
}
""".trimIndent()
        )
    )

    val keyProjects = listOf(
        ProjectItem(
            id = "ultrasonic_yolo",
            title = "Ultrasonic Anomaly Recognition Engine",
            subtitle = "Custom YOLO-style CNN for Acoustic Flaw Detection",
            description = "End-to-end computer vision pipeline converting raw ultrasonic A-Scan / B-Scan acoustic pulse waveforms into 2D time-frequency tensors, followed by deep learning inference to identify internal rail fissures, welds, and material anomalies in real-time.",
            metrics = listOf(
                "Accuracy" to "98.4%",
                "Inference Latency" to "3.8 ms",
                "Deployment" to "On-Train Edge NPU",
                "Dataset" to "10,000+ Track Miles"
            ),
            techStack = listOf("PyTorch", "YOLO Architecture", "C++ Inference Engine", "OpenCV", "Signal Processing"),
            keyInnovation = "Treating acoustic waveforms as optical spatial representations to unlock high-speed convolution filters on non-visual physics signals."
        ),
        ProjectItem(
            id = "lidar_simulator",
            title = "Multi-Sensor LiDAR Ray-Tracing Simulator",
            subtitle = "Physics-Informed Optical Propagation & Point-Cloud Engine",
            description = "Modular multi-threaded C++ simulator replicating laser beam divergence, surface roughness reflectance (Lambertian & specular), atmospheric attenuation, and occlusion to stress-test spatial perception networks before field trials.",
            metrics = listOf(
                "Throughput" to "2.4M Rays/sec",
                "Threads" to "Dynamic OpenMP Pool",
                "Point Accuracy" to "< 1.2 mm error",
                "Sensors" to "Velodyne & Solid-State"
            ),
            techStack = listOf("C++20", "Ray-Tracing Optics", "Spatial Octrees", "Eigen", "OpenMP"),
            keyInnovation = "Exact geometric beam divergence modeling that produces true-to-life point return densities across varying incidence angles."
        ),
        ProjectItem(
            id = "volumetric_processor",
            title = "Automated Volumetric Point-Cloud Processor",
            subtitle = "3D Surface-Mesh & Density Clustering Engine",
            description = "High-speed 3D point-cloud analytical compute replacing manual surveying. Employs Poisson surface reconstruction, alpha-shape boundary clipping, and Delaunay triangulation to measure rail ballast, tunnel clearances, and material excavation volumes.",
            metrics = listOf(
                "Speedup" to "18x vs Manual",
                "Volume Variance" to "< 0.8%",
                "Cloud Scale" to "500M+ Points",
                "Format" to "LAS / PCD / Custom Binary"
            ),
            techStack = listOf("C++17", "Point Cloud Library (PCL)", "Delaunay Triangulation", "CUDA"),
            keyInnovation = "Out-of-core spatial chunking allowing multi-gigabyte survey files to process on constrained embedded edge memory."
        )
    )

    val education = listOf(
        mapOf(
            "degree" to "Master of Science, Computational Chemical Physics",
            "school" to "University of California San Diego (UCSD)",
            "highlights" to "Advanced numerical methods, quantum & molecular dynamics, high-performance computing, numerical optimization."
        ),
        mapOf(
            "degree" to "Bachelor of Science in Engineering, Chemical Engineering",
            "school" to "University of Iowa",
            "highlights" to "Fluid mechanics, transport phenomena, numerical algorithms, thermodynamics."
        )
    )

    val skillCategories = listOf(
        SkillCategory(
            title = "Machine Learning & Perception",
            iconName = "Psychology",
            skills = listOf(
                SkillItem("Custom CNNs & YOLO Architectures", 96, "Ultrasonic & spatial vision"),
                SkillItem("Computer Vision & Feature Extraction", 94, "OpenCV, spatial transforms"),
                SkillItem("Synthetic Data Generation", 95, "Physics-informed ray-tracing"),
                SkillItem("Edge AI Inference Optimization", 92, "FP16 / INT8 quantization, TensorRT"),
                SkillItem("PyTorch & TorchScript", 90, "Custom loss functions, training loops")
            )
        ),
        SkillCategory(
            title = "Languages & High-Performance Compute",
            iconName = "Code",
            skills = listOf(
                SkillItem("C++ (C++17 / C++20)", 98, "Modern templates, lock-free, zero-copy"),
                SkillItem("Python & Scientific Stack", 95, "NumPy, SciPy, Matplotlib"),
                SkillItem("Fortran & Legacy HPC", 85, "Scientific simulations, BLAS/LAPACK"),
                SkillItem("Rust & Memory-Safe Systems", 82, "Embedded crates & concurrency"),
                SkillItem("CUDA & GPU Acceleration", 88, "Parallel kernels & shared memory")
            )
        ),
        SkillCategory(
            title = "Simulation, Edge & Telemetry",
            iconName = "Memory",
            skills = listOf(
                SkillItem("Hardware-in-the-Loop (HIL/CHIL)", 96, "RTX guidance testbeds"),
                SkillItem("LiDAR & Spatial Sensor Processing", 95, "Beam divergence, point clouds"),
                SkillItem("Embedded OS & ChannelSON", 92, "Flash memory event dispatching"),
                SkillItem("Low-Latency Event APIs", 94, "Northrop inter-board communications"),
                SkillItem("ROS / Robotics Middleware", 88, "Nodes, transforms, pub/sub")
            )
        ),
        SkillCategory(
            title = "Data Engineering & Distributed Systems",
            iconName = "Storage",
            skills = listOf(
                SkillItem("High-Throughput Telemetry Ingestion", 93, "Deterministic messaging pipelines"),
                SkillItem("Elasticsearch, MongoDB & RabbitMQ", 90, "Distributed event streaming"),
                SkillItem("CMake, Docker & CI/CD", 92, "Cross-compilation toolchains"),
                SkillItem("Multi-Terabyte HPC Processing", 94, "OpenMPI cluster data analysis")
            )
        )
    )

    val projectCategories = listOf(
        ProjectCategoryData(
            id = "ml_perception",
            name = "Machine Learning & Vision",
            projectCount = 6,
            experiencePercentage = 35,
            proficiencyScore = 96,
            color = androidx.compose.ui.graphics.Color(0xFF00E5FF), // CyanNeon
            keyTech = listOf("PyTorch", "YOLO CNNs", "OpenCV", "TensorRT", "Spectrograms")
        ),
        ProjectCategoryData(
            id = "systems_hpc",
            name = "Languages & High-Perf Compute",
            projectCount = 8,
            experiencePercentage = 28,
            proficiencyScore = 98,
            color = androidx.compose.ui.graphics.Color(0xFF10B981), // EmeraldTelemetry
            keyTech = listOf("C++20", "CUDA", "OpenMP", "Python", "MPI Clusters")
        ),
        ProjectCategoryData(
            id = "sim_edge",
            name = "Simulation, HIL & Edge",
            projectCount = 5,
            experiencePercentage = 22,
            proficiencyScore = 95,
            color = androidx.compose.ui.graphics.Color(0xFFF59E0B), // AmberSignal
            keyTech = listOf("HIL/CHIL", "Ray-Tracing", "ChannelSON RTOS", "LiDAR Arrays")
        ),
        ProjectCategoryData(
            id = "data_telemetry",
            name = "Telemetry & Distributed Systems",
            projectCount = 4,
            experiencePercentage = 15,
            proficiencyScore = 92,
            color = androidx.compose.ui.graphics.Color(0xFFA855F7), // VioletPulse
            keyTech = listOf("Zero-Copy Queues", "RabbitMQ", "Elasticsearch", "Docker")
        )
    )

    val topSkillsMetrics = listOf(
        SkillMetric("C++ (C++17 / C++20)", "High-Performance Systems", 98, 7.5, "Lock-free ring buffers, zero-copy", androidx.compose.ui.graphics.Color(0xFF10B981)),
        SkillMetric("Custom CNNs & YOLO", "Machine Learning & Perception", 96, 5.0, "Ultrasonic & spatial acoustic tensors", androidx.compose.ui.graphics.Color(0xFF00E5FF)),
        SkillMetric("HIL / CHIL Simulation", "Simulation & Guidance", 96, 4.0, "Hardware-in-the-loop synchronized telemetry", androidx.compose.ui.graphics.Color(0xFFF59E0B)),
        SkillMetric("Python Scientific Stack", "Machine Learning & HPC", 95, 8.0, "NumPy, SciPy, PyTorch, statistical mechanics", androidx.compose.ui.graphics.Color(0xFF00E5FF)),
        SkillMetric("LiDAR Ray-Tracing", "Perception & Simulation", 95, 4.5, "Beam divergence, 3D point clouds", androidx.compose.ui.graphics.Color(0xFF00E5FF)),
        SkillMetric("Low-Latency Event APIs", "Embedded Systems", 94, 5.5, "Deterministic inter-board communications", androidx.compose.ui.graphics.Color(0xFF10B981)),
        SkillMetric("Distributed Telemetry", "Distributed Systems", 93, 4.0, "Deterministic messaging pipelines", androidx.compose.ui.graphics.Color(0xFFA855F7)),
        SkillMetric("ChannelSON RTOS", "Embedded Firmware", 92, 3.0, "In-flash serial parser & zero-alloc dispatch", androidx.compose.ui.graphics.Color(0xFFF59E0B))
    )
}
