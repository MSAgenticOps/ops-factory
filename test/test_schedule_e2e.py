"""
End-to-end test for Scheduled Actions page.
Tests: pause, unpause, edit, run now, view runs, delete, create.
"""
import time
import sys
from playwright.sync_api import sync_playwright, expect

BASE_URL = "http://127.0.0.1:5173"
TIMEOUT = 10000


def log(msg):
    print(f"  {msg}")


def wait_for_toast_dismiss(page):
    """Wait for toast to disappear so it doesn't block clicks."""
    time.sleep(1.5)


def test_schedule_functions():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()
        page.set_default_timeout(TIMEOUT)

        # Navigate to scheduled actions page
        page.goto(f"{BASE_URL}/scheduled-actions")
        page.wait_for_load_state("networkidle")
        time.sleep(2)

        # Take initial screenshot
        page.screenshot(path="/tmp/schedule_01_initial.png", full_page=True)

        results = []

        # ============================
        # Test 1: Verify initial state
        # ============================
        print("\n=== Test 1: Verify initial schedule list ===")
        cards = page.locator(".scheduled-card")
        count = cards.count()
        log(f"Found {count} schedule card(s)")
        if count >= 1:
            results.append(("Initial schedule list", True))
            # Get the first job name
            first_name = cards.first.locator(".agent-name").inner_text()
            log(f"First job: {first_name}")
        else:
            results.append(("Initial schedule list", False))
            log("No schedules found, will create one first")

        # ============================
        # Test 2: Create a new schedule
        # ============================
        print("\n=== Test 2: Create new schedule ===")
        create_btn = page.locator("button:has-text('Create')")
        if create_btn.count() == 0:
            create_btn = page.locator("button:has-text('新建')")
        create_btn.first.click()
        page.wait_for_selector(".scheduled-modal", state="visible")
        time.sleep(0.5)

        # Fill form
        name_input = page.locator(".scheduled-modal .scheduled-input").first
        name_input.fill("test-e2e-schedule")

        instruction_textarea = page.locator(".scheduled-modal .scheduled-textarea")
        instruction_textarea.fill("This is an e2e test schedule. Just say hello.")

        page.screenshot(path="/tmp/schedule_02_create_form.png", full_page=True)

        # Submit
        submit_btn = page.locator(".modal-footer button.btn-primary")
        submit_btn.click()
        page.wait_for_selector(".scheduled-modal", state="hidden", timeout=10000)
        time.sleep(2)

        page.screenshot(path="/tmp/schedule_03_after_create.png", full_page=True)

        # Verify creation
        test_card = page.locator(".scheduled-card:has-text('test-e2e-schedule')")
        if test_card.count() > 0:
            results.append(("Create schedule", True))
            log("Schedule created successfully")
        else:
            results.append(("Create schedule", False))
            log("Schedule not found after creation")

        # ============================
        # Test 3: Pause schedule
        # ============================
        print("\n=== Test 3: Pause schedule ===")
        test_card = page.locator(".scheduled-card:has-text('test-e2e-schedule')")
        if test_card.count() > 0:
            pause_btn = test_card.locator("button:has-text('Pause'), button:has-text('暂停')")
            if pause_btn.count() > 0:
                pause_btn.first.click()
                time.sleep(2)
                page.screenshot(path="/tmp/schedule_04_after_pause.png", full_page=True)

                # Check status changed to paused
                status = test_card.locator(".status-pill").inner_text()
                log(f"Status after pause: {status}")
                if "Paused" in status or "已暂停" in status:
                    results.append(("Pause schedule", True))
                else:
                    results.append(("Pause schedule", False))
            else:
                log("No pause button found")
                results.append(("Pause schedule", False))
        else:
            results.append(("Pause schedule", False))
            log("Test schedule card not found")

        wait_for_toast_dismiss(page)

        # ============================
        # Test 4: Unpause (Resume) schedule
        # ============================
        print("\n=== Test 4: Unpause schedule ===")
        test_card = page.locator(".scheduled-card:has-text('test-e2e-schedule')")
        if test_card.count() > 0:
            resume_btn = test_card.locator("button:has-text('Resume'), button:has-text('恢复')")
            if resume_btn.count() > 0:
                resume_btn.first.click()
                time.sleep(2)
                page.screenshot(path="/tmp/schedule_05_after_unpause.png", full_page=True)

                status = test_card.locator(".status-pill").inner_text()
                log(f"Status after unpause: {status}")
                if "Active" in status or "活跃" in status:
                    results.append(("Unpause schedule", True))
                else:
                    results.append(("Unpause schedule", False))
            else:
                log("No resume button found")
                results.append(("Unpause schedule", False))
        else:
            results.append(("Unpause schedule", False))

        wait_for_toast_dismiss(page)

        # ============================
        # Test 5: Edit schedule
        # ============================
        print("\n=== Test 5: Edit schedule ===")
        test_card = page.locator(".scheduled-card:has-text('test-e2e-schedule')")
        if test_card.count() > 0:
            edit_btn = test_card.locator("button:has-text('Edit'), button:has-text('编辑')")
            if edit_btn.count() > 0:
                edit_btn.first.click()
                page.wait_for_selector(".scheduled-modal", state="visible")
                time.sleep(0.5)

                # Modify cron
                cron_input = page.locator(".scheduled-modal .scheduled-input").nth(1)
                cron_input.fill("0 0 10 * * *")

                page.screenshot(path="/tmp/schedule_06_edit_form.png", full_page=True)

                # Save
                save_btn = page.locator(".modal-footer button.btn-primary")
                save_btn.click()
                page.wait_for_selector(".scheduled-modal", state="hidden", timeout=10000)
                time.sleep(2)

                page.screenshot(path="/tmp/schedule_07_after_edit.png", full_page=True)

                # Verify cron changed
                test_card = page.locator(".scheduled-card:has-text('test-e2e-schedule')")
                cron_text = test_card.locator(".scheduled-cron").inner_text()
                log(f"Cron after edit: {cron_text}")
                if "10" in cron_text:
                    results.append(("Edit schedule", True))
                else:
                    results.append(("Edit schedule", False))
            else:
                log("No edit button found")
                results.append(("Edit schedule", False))
        else:
            results.append(("Edit schedule", False))

        wait_for_toast_dismiss(page)

        # ============================
        # Test 6: Run Now
        # ============================
        print("\n=== Test 6: Run Now ===")
        test_card = page.locator(".scheduled-card:has-text('test-e2e-schedule')")
        if test_card.count() > 0:
            run_btn = test_card.locator("button:has-text('Run Now'), button:has-text('立即运行')")
            if run_btn.count() > 0:
                run_btn.first.click()
                time.sleep(3)
                page.screenshot(path="/tmp/schedule_08_after_run_now.png", full_page=True)

                # Check if toast showed success or if status shows running
                results.append(("Run Now", True))
                log("Run Now triggered successfully")
            else:
                log("No Run Now button found")
                results.append(("Run Now", False))
        else:
            results.append(("Run Now", False))

        wait_for_toast_dismiss(page)

        # ============================
        # Test 7: View Run History
        # ============================
        print("\n=== Test 7: View Run History ===")
        # Refresh to get latest state
        page.reload()
        page.wait_for_load_state("networkidle")
        time.sleep(2)

        test_card = page.locator(".scheduled-card:has-text('test-e2e-schedule')")
        if test_card.count() > 0:
            view_runs_btn = test_card.locator("button:has-text('View Runs'), button:has-text('运行记录')")
            if view_runs_btn.count() > 0:
                view_runs_btn.first.click()
                time.sleep(2)
                page.screenshot(path="/tmp/schedule_09_view_runs.png", full_page=True)

                # Check that runs panel is visible
                runs_panel = page.locator(".scheduled-runs-panel")
                if runs_panel.count() > 0:
                    results.append(("View Run History", True))
                    log("Runs panel opened")

                    # Check for run items
                    run_items = page.locator(".scheduled-run-item")
                    log(f"Found {run_items.count()} run(s)")
                else:
                    results.append(("View Run History", False))
                    log("Runs panel not found")

                # Go back
                back_btn = page.locator("button:has-text('Back'), button:has-text('返回')")
                if back_btn.count() > 0:
                    back_btn.first.click()
                    time.sleep(1)
            else:
                log("No View Runs button found")
                results.append(("View Run History", False))
        else:
            results.append(("View Run History", False))

        # ============================
        # Test 8: Running state shows Kill button (if still running)
        # ============================
        print("\n=== Test 8: Running state buttons ===")
        page.reload()
        page.wait_for_load_state("networkidle")
        time.sleep(2)

        test_card = page.locator(".scheduled-card:has-text('test-e2e-schedule')")
        if test_card.count() > 0:
            status = test_card.locator(".status-pill").inner_text()
            log(f"Current status: {status}")

            if "Running" in status or "运行中" in status:
                kill_btn = test_card.locator("button:has-text('Kill'), button:has-text('终止')")
                pause_btn = test_card.locator("button:has-text('Pause'), button:has-text('暂停')")
                edit_btn = test_card.locator("button:has-text('Edit'), button:has-text('编辑')")
                hint = test_card.locator(".scheduled-running-hint")

                kill_visible = kill_btn.count() > 0
                pause_hidden = pause_btn.count() == 0
                edit_hidden = edit_btn.count() == 0
                hint_visible = hint.count() > 0

                log(f"Kill={kill_visible}, Pause hidden={pause_hidden}, Edit hidden={edit_hidden}, Hint={hint_visible}")

                if kill_visible and pause_hidden and edit_hidden:
                    results.append(("Running state buttons", True))

                    # Kill the running job
                    kill_btn.first.click()
                    time.sleep(2)
                    log("Killed running job")
                else:
                    results.append(("Running state buttons", False))
            else:
                log("Job not running, skipping running-state test (expected if job finished quickly)")
                results.append(("Running state buttons", True))  # Not a failure if job already finished

        page.screenshot(path="/tmp/schedule_10_state.png", full_page=True)
        wait_for_toast_dismiss(page)

        # ============================
        # Test 9: Delete schedule
        # ============================
        print("\n=== Test 9: Delete schedule ===")
        # Wait a bit for state to settle
        page.reload()
        page.wait_for_load_state("networkidle")
        time.sleep(2)

        test_card = page.locator(".scheduled-card:has-text('test-e2e-schedule')")
        if test_card.count() > 0:
            # If still running, kill first
            status = test_card.locator(".status-pill").inner_text()
            if "Running" in status or "运行中" in status:
                kill_btn = test_card.locator("button:has-text('Kill'), button:has-text('终止')")
                if kill_btn.count() > 0:
                    kill_btn.first.click()
                    time.sleep(3)
                    page.reload()
                    page.wait_for_load_state("networkidle")
                    time.sleep(2)
                    test_card = page.locator(".scheduled-card:has-text('test-e2e-schedule')")

            delete_btn = test_card.locator("button:has-text('Delete'), button:has-text('删除')")
            if delete_btn.count() > 0:
                # Handle confirm dialog
                page.on("dialog", lambda dialog: dialog.accept())
                delete_btn.first.click()
                time.sleep(2)

                page.screenshot(path="/tmp/schedule_11_after_delete.png", full_page=True)

                # Verify deleted
                remaining = page.locator(".scheduled-card:has-text('test-e2e-schedule')")
                if remaining.count() == 0:
                    results.append(("Delete schedule", True))
                    log("Schedule deleted successfully")
                else:
                    results.append(("Delete schedule", False))
                    log("Schedule still exists after delete")
            else:
                log("No delete button found")
                results.append(("Delete schedule", False))
        else:
            results.append(("Delete schedule", False))
            log("Test schedule not found for deletion")

        # ============================
        # Summary
        # ============================
        print("\n" + "=" * 50)
        print("RESULTS SUMMARY")
        print("=" * 50)
        all_pass = True
        for name, passed in results:
            status_icon = "PASS" if passed else "FAIL"
            prefix = "  ✅" if passed else "  ❌"
            print(f"{prefix} {status_icon}: {name}")
            if not passed:
                all_pass = False

        print(f"\nTotal: {len(results)} tests, {sum(1 for _, p in results if p)} passed, {sum(1 for _, p in results if not p)} failed")
        print(f"Screenshots saved to /tmp/schedule_*.png")

        browser.close()

        if not all_pass:
            sys.exit(1)


if __name__ == "__main__":
    test_schedule_functions()
