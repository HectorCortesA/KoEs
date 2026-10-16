import Foundation
import WidgetKit

/// Añade KoesWidgetRefreshObserver.shared.start() al init de tu App de SwiftUI.
@MainActor
final class KoesWidgetRefreshObserver {
    static let shared = KoesWidgetRefreshObserver()
    private var token: NSObjectProtocol?
    private init() {}

    func start() {
        guard token == nil else { return }
        token = NotificationCenter.default.addObserver(
            forName: Notification.Name("KOES_WIDGET_UPDATED"),
            object: nil,
            queue: .main
        ) { _ in
            WidgetCenter.shared.reloadTimelines(ofKind: "KoesCalendarWidget")
        }
    }
}
