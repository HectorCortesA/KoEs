import SwiftUI
import WidgetKit

// Usa exactamente el mismo App Group en la app y en la Widget Extension.
private let appGroupID = "group.com.hector.koes"
private let widgetKind = "KoesCalendarWidget"
private let countsKey = "daily_word_counts"

private func koesColor(_ value: UInt32) -> Color {
    Color(
        red: Double((value >> 16) & 0xFF) / 255.0,
        green: Double((value >> 8) & 0xFF) / 255.0,
        blue: Double(value & 0xFF) / 255.0
    )
}

private func loadKoesCounts() -> [Int] {
    let saved = UserDefaults(suiteName: appGroupID)?.string(forKey: countsKey) ?? ""
    let values = saved.split(separator: ",").map { max(0, Int($0) ?? 0) }
    return (0..<60).map { values.indices.contains($0) ? values[$0] : 0 }
}

struct KoesCalendarEntry: TimelineEntry {
    let date: Date
    let counts: [Int]
}

struct KoesCalendarProvider: TimelineProvider {
    func placeholder(in context: Context) -> KoesCalendarEntry {
        var example = Array(repeating: 0, count: 60)
        example[0] = 1
        example[1] = 5
        example[2] = 10
        return KoesCalendarEntry(date: Date(), counts: example)
    }

    func getSnapshot(in context: Context, completion: @escaping (KoesCalendarEntry) -> Void) {
        completion(KoesCalendarEntry(date: Date(), counts: loadKoesCounts()))
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<KoesCalendarEntry>) -> Void) {
        let now = Date()
        let entry = KoesCalendarEntry(date: now, counts: loadKoesCounts())
        // Actualización periódica de respaldo. También se solicita una recarga al guardar datos.
        completion(Timeline(entries: [entry], policy: .after(now.addingTimeInterval(30 * 60))))
    }
}

private struct KoesFlower: View {
    let count: Int
    let accented: Bool

    private var palette: (Color, Color, Color) {
        if accented {
            return (.white, .white.opacity(0.7), .white)
        }
        if count >= 10 {
            return (koesColor(0x7A287A), koesColor(0x6B1863), koesColor(0xF7CFE1))
        }
        if count >= 5 {
            return (koesColor(0xC058A8), koesColor(0xA84893), koesColor(0xF7CFE1))
        }
        return (koesColor(0xF7CFE1), koesColor(0xE882AD), koesColor(0xC75E5B))
    }

    var body: some View {
        Canvas { context, size in
            let side = min(size.width, size.height) * 0.85
            let cx = size.width / 2
            let cy = size.height / 2
            let radius = side * 0.28
            let distance = side * 0.26
            let colors = palette
            let angles = [-90.0, -18.0, 54.0, 126.0, 198.0]
            for (index, degrees) in angles.enumerated() {
                let angle = degrees * .pi / 180.0
                let px = cx + distance * cos(angle)
                let py = cy + distance * sin(angle)
                let path = Path(ellipseIn: CGRect(
                    x: px - radius, y: py - radius,
                    width: radius * 2, height: radius * 2
                ))
                context.fill(path, with: .color(index.isMultiple(of: 2) ? colors.0 : colors.1))
            }
            let centerRadius = side * 0.16
            context.fill(
                Path(ellipseIn: CGRect(
                    x: cx - centerRadius, y: cy - centerRadius,
                    width: centerRadius * 2, height: centerRadius * 2
                )),
                with: .color(colors.2)
            )
        }
    }
}

private struct KoesActivityCell: View {
    let count: Int
    let accented: Bool

    var body: some View {
        GeometryReader { geometry in
            if count > 0 {
                KoesFlower(count: count, accented: accented)
                    .frame(width: geometry.size.width, height: geometry.size.height)
            } else {
                RoundedRectangle(cornerRadius: 3)
                    .fill(accented ? Color.white.opacity(0.26) : koesColor(0xB3D2DC))
            }
        }
    }
}

struct KoesCalendarWidgetView: View {
    let entry: KoesCalendarEntry
    @Environment(\.widgetRenderingMode) private var renderingMode

    var body: some View {
        if #available(iOS 17.0, *) {
            calendarGrid
                .containerBackground(for: .widget) {
                    if #available(iOS 26.0, *) {
                        // WidgetKit aplica Liquid Glass en la apariencia
                        // transparente o tintada de la pantalla de inicio.
                        Rectangle().fill(.ultraThinMaterial)
                    } else {
                        // iOS 17 y 18: igual que Android.
                        Color.white
                    }
                }
        } else {
            // iOS 16: containerBackground(for: .widget) aún no existe.
            calendarGrid.background(Color.white)
        }
    }

    private var usesSystemAccent: Bool {
        if #available(iOS 26.0, *) {
            return renderingMode == .accented
        }
        return false
    }

    private var calendarGrid: some View {
        GeometryReader { geometry in
            let w = geometry.size.width
            let h = geometry.size.height
            let padding = min(12.0, min(w, h) * 0.06)
            let gap = min(5.0, min(w, h) * 0.026)
            let cellW = max(1.0, min(
                (w - 2 * padding - 11 * gap) / 12,
                ((h - 2 * padding - 4 * gap) / 5) * 23.0 / 27.0
            ))
            let cellH = cellW * 27.0 / 23.0
            let totalW = 12 * cellW + 11 * gap
            let totalH = 5 * cellH + 4 * gap
            let left = (w - totalW) / 2
            let top = (h - totalH) / 2

            ZStack(alignment: .topLeading) {
                ForEach(0..<60, id: \.self) { index in
                    let col = index / 5
                    let row = index % 5
                    KoesActivityCell(
                        count: entry.counts[index],
                        accented: usesSystemAccent
                    )
                    .frame(width: cellW, height: cellH)
                    .position(
                        x: left + CGFloat(col) * (cellW + gap) + cellW / 2,
                        y: top + CGFloat(row) * (cellH + gap) + cellH / 2
                    )
                }
            }
            .frame(width: w, height: h)
        }
    }
}

struct KoesCalendarWidget: Widget {
    var body: some WidgetConfiguration {
        StaticConfiguration(kind: widgetKind, provider: KoesCalendarProvider()) { entry in
            KoesCalendarWidgetView(entry: entry)
        }
        .configurationDisplayName("KOES · Mini Calendario")
        .description("Tus flores de actividad y palabras aprendidas.")
        .supportedFamilies([.systemMedium])
        .contentMarginsDisabled()
    }
}

// Si el asistente de Xcode creó un archivo con otro @main, elimina ese @main:
// cada Widget Extension solo puede tener uno.
@main
struct KoesCalendarWidgetBundle: WidgetBundle {
    var body: some Widget {
        KoesCalendarWidget()
    }
}
