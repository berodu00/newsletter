import { test, expect } from '@playwright/test';

test.describe('Basic Health Check', () => {
    test('should load login page', async ({ page }) => {
        await page.goto('/login');
        // Check for unique text on login page
        await expect(page.getByText('로그인', { exact: true })).toBeVisible();
        await expect(page.locator('input[type="password"]')).toBeVisible();
    });
});
